package com.rocketchain.storage;

public class DB {
    public final static byte BLOCK_INFO = (byte)'b';
    public final static byte TRANSACTION = (byte)'t';
    public final static byte BLOCK_FILE_INFO = (byte)'f';
    public final static byte LAST_BLOCK_FILE = (byte)'l';
    public final static byte BEST_BLOCK_HASH = (byte)'B';
    public final static byte BLOCK_HEIGHT = (byte)'h';


    // The disk-pool, which keeps transactions on disk instead of mempool.
    public final static byte TRANSACTION_POOL = (byte)'d';
    // The index from transaction creation time to the transaction hash.
    public final static byte TRANSACTION_TIME = (byte)'e';
}
