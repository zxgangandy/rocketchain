package com.rocketchain.storage.index;

import java.io.File;

public class DatabaseFactory {
    public static KeyValueDatabase create(File path){
        return new RocksDatabase(path);
    }
}
