package com.rocketchain.storage.index;

import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.StorageException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.RocksIterator;

public class RocksDatabaseIterator implements ClosableIterator<Pair<byte[], byte[]>> {
    private RocksIterator rocksIterator;

    private boolean isClosed;

    public RocksDatabaseIterator(RocksIterator rocksIterator) {
        this.rocksIterator = rocksIterator;
    }

    @Override
    public boolean hasNext() {
        if (isClosed) {
            return false;
        } else {
            return rocksIterator.isValid();
        }
    }

    @Override
    public Pair<byte[], byte[]> next() {
        assert (!isClosed);

        if (!rocksIterator.isValid()) {
            throw new StorageException(ErrorCode.NoMoreKeys);
        }

        byte[] rawKey = rocksIterator.key();
        byte[] rawValue = rocksIterator.value();

        rocksIterator.next();

        return new MutablePair<>(rawKey, rawValue);
    }

    @Override
    public void close() {
        rocksIterator.close();
        isClosed = true;
    }
}
