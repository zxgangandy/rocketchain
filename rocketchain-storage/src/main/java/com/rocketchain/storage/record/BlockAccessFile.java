package com.rocketchain.storage.record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class BlockAccessFile {
    private RandomAccessFile file;
    private FileChannel fileChannel;

    public BlockAccessFile(File path, Long maxFileSize) throws IOException {
        file = new RandomAccessFile(path, "rw");
        fileChannel = file.getChannel();
    }

    public long size()  {

        try {
            return fileChannel.size();
        } catch (IOException e) {
            throw new IllegalStateException("Get file size error");
        }
    }

    public long offset() {
        try {
            return fileChannel.position();
        } catch (IOException e) {
            throw new IllegalStateException("Set file position error");
        }
    }

    public void moveTo(long offset)  {
        try {
            if (fileChannel.position() != offset) {
                fileChannel.position(offset);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Move file position error");
        }
    }

    public ByteBuffer read(long offset, int size)  {
        moveTo(offset);

        ByteBuffer buffer = ByteBuffer.allocate(size);

        try {
            fileChannel.read(buffer);
        } catch (IOException e) {
            throw new IllegalStateException("Read file size error");
        }

        return buffer;
    }

    public void append(ByteBuffer buffer)  {
        // If we are not at the end of the file, move to the end of it.
        if (offset() != size()) {
            moveTo(size());
        }

        try {
            fileChannel.write(buffer);
        } catch (IOException e) {
            throw new IllegalStateException("Write file size error");
        }
    }

    public void flush()  {
        try {
            fileChannel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close()  {
        try {
            fileChannel.force(true);
            fileChannel.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
