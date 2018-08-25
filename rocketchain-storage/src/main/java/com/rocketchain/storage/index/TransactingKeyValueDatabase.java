package com.rocketchain.storage.index;

public interface TransactingKeyValueDatabase extends KeyValueDatabase {

    void beginTransaction();

    void abortTransaction();

    void commitTransaction();

}
