package com.rocketchain.storage;

import org.rocksdb.RocksDB;

/**
 * @author
 */
public class Storage {

    private static Boolean isInitialized = false;

    public static boolean initialized() {
        return isInitialized;
    }

    public static void initialize() {
        RocksDB.loadLibrary();
        isInitialized = true;
    }
}
