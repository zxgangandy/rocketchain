package com.rocketchain.storage.index;

import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatchWithIndex;
import org.rocksdb.WriteOptions;

import java.util.HashMap;
import java.util.Map;

public class TransactingRocksDatabase implements TransactingKeyValueDatabase {
    private RocksDatabase db;
    private WriteBatchWithIndex writeBatch;
    private Map<byte[], byte[]> putCache;
    private Map<byte[], Void> delCache;

    public TransactingRocksDatabase(RocksDatabase db) {
        this.db = db;
    }


    @Override
    public void beginTransaction() {
        writeBatch = new WriteBatchWithIndex(true);

        putCache = new HashMap<>();
        delCache = new HashMap<>();
    }

    @Override
    public void abortTransaction() {
        assert(writeBatch != null);
        writeBatch = null;
        putCache = null;
        delCache = null;
    }

    @Override
    public void commitTransaction() {
        assert(writeBatch != null);

        WriteOptions writeOptions = new WriteOptions();
        writeOptions.setSync(true);

        try {
            db.getDb().write(writeOptions, writeBatch);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        writeBatch = null;
        putCache = null;
        delCache = null;
    }

    @Override
    public ClosableIterator<Pair<byte[], byte[]>> seek(byte[] keyOption) {
        RocksIterator rocksIterator;
        if (writeBatch != null) {
            rocksIterator = db.getDb().newIterator();
            writeBatch.newIteratorWithBase(rocksIterator);
        } else {
            rocksIterator = db.getDb().newIterator();
        }

        return db.seek(rocksIterator, keyOption);
    }

    @Override
    public byte[] get(byte[] key) {
        if (delCache == null || putCache == null) {
            return db.get(key);
        } else {
            if (delCache.containsKey(key)) {
                return null;
            } else {
                byte[] value = putCache.get(key);
                if (value != null) {
                    return value;
                } else {
                    return db.get(key);
                }
            }
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        assert(writeBatch != null);
        try {
            writeBatch.put(key, value);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        putCache.put(key, value);
        delCache.remove(key);
    }

    @Override
    public void del(byte[] key) {
        assert(writeBatch != null);
        try {
            writeBatch.delete(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        delCache.put(key, null);
        putCache.remove(key);
    }

    @Override
    public void close() {

    }

    @Override
    public TransactingKeyValueDatabase transacting() {
        return null;
    }
}
