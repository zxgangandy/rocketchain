package com.rocketchain.storage.record;

import com.rocketchain.codec.Codec;
import com.rocketchain.proto.RecordLocator;
import com.rocketchain.storage.exception.BlockStorageException;
import com.rocketchain.utils.exception.ErrorCode;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RecordFile extends BlockAccessFile {
    private File path;
    private long maxFileSize;

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public RecordFile(File path, Long maxFileSize) throws IOException {
        super(path, maxFileSize);

        this.maxFileSize = maxFileSize;
        this.path = path;

        moveTo(size());
    }

    public <T> T readRecord(Codec<T> codec, RecordLocator locator) {
        rwLock.readLock().lock();
        try {
            byte[] buffer = read(locator.getOffset(), locator.getSize()).array();
            return codec.decode(buffer);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public <T> RecordLocator appendRecord(Codec<T> codec, T record) {
        rwLock.writeLock().lock();

        try {
            // Move to the end of the file if we are not.
            if (offset() <= size()) {
                moveTo(size());
            }

            byte[] serializedBytes = codec.encode(record);
            long initialOffset = offset();
            ByteBuffer buffer = ByteBuffer.wrap(serializedBytes);
            if (initialOffset + buffer.capacity() > maxFileSize) {
                throw new BlockStorageException(ErrorCode.OutOfFileSpace);
            }
            append(buffer);
            return new RecordLocator(initialOffset, buffer.capacity());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

}
