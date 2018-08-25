package com.rocketchain.storage.index;

import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RocksDatabase implements KeyValueDatabase {
    private final Logger logger = LoggerFactory.getLogger(RocksDatabase.class);

    private String dbAbsolutePath;
    private  Options options;
    private RocksDB db;

    public RocksDatabase(File path) {
        dbAbsolutePath = path.getAbsolutePath();

        options = new Options()
                        .setCreateIfMissing(true)
                        .setCreateMissingColumnFamilies(true)
                        .setWriteBufferSize(256 * 1024 * 1024)
                        .setMaxWriteBufferNumber(4)
                        .setMinWriteBufferNumberToMerge(2)
                        .setMaxOpenFiles(5000)
                        .setMaxBackgroundCompactions(3) // how many cores to allocate to compaction?
                        .setMaxBackgroundFlushes(1);

        options.getEnv().setBackgroundThreads(3, Env.COMPACTION_POOL)
                .setBackgroundThreads(1, Env.FLUSH_POOL);

        try {
            db = RocksDB.open(options, dbAbsolutePath);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }




    /** Seek a key greater than or equal to the given key.
     * Return an iterator which iterates each (key, value) pair from the seek position.
     *
     * @param keyOption if Some(key) seek a key greater than or equal to the key; Seek all keys and values otherwise.
     * @return An Iterator to iterate (key, value) pairs.
     */
    @Override
    public ClosableIterator<Pair<byte[], byte[]>> seek(byte[] keyOption) {
        RocksIterator rocksIterator =  db.newIterator();

        return seek(rocksIterator, keyOption);
    }

    /**
     * Seek a key greater than or equal to the given key.
     * Return an iterator which iterates each (key, value) pair from the seek position.
     *
     * @param rocksIterator The iterator to use for seeking a key.
     * @param keyOption     if Some(key) seek a key greater than or equal to the key; Seek all keys and values otherwise.
     * @return An Iterator to iterate (key, value) pairs.
     */
    public ClosableIterator<Pair<byte[], byte[]>> seek(RocksIterator rocksIterator, byte[] keyOption) {
        if (keyOption != null) {
            rocksIterator.seek(keyOption);
        } else {
            rocksIterator.seekToFirst();
        }

        return new RocksDatabaseIterator(rocksIterator);
    }

    @Override
    public byte[] get(byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void put(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void del(byte[] key) {
        try {
            db.delete(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (db != null) {
            db.close();
            options.close();
        }

        db = null;
        options = null;
    }

    @Override
    public TransactingKeyValueDatabase transacting() {
        return null;
    }

    public RocksDB getDb() {
        return db;
    }
}
