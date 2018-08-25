package com.rocketchain.storage;

import com.rocketchain.proto.Hash;
import com.rocketchain.proto.FileRecordLocator;

public class TransactionLocator {
    private Hash txHash;
    private FileRecordLocator txLocator ;

    public TransactionLocator(Hash txHash, FileRecordLocator txLocator) {
        this.txHash = txHash;
        this.txLocator = txLocator;
    }

    public Hash getTxHash() {
        return txHash;
    }

    public FileRecordLocator getTxLocator() {
        return txLocator;
    }
}
