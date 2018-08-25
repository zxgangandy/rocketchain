package com.rocketchain.storage;

import com.rocketchain.proto.FileRecordLocator;

import java.util.List;

public class AppendBlockResult {
    private FileRecordLocator blockLocator ;
    private FileRecordLocator headerLocator ;
    private List<TransactionLocator> txLocators;

    public AppendBlockResult(FileRecordLocator blockLocator , FileRecordLocator headerLocator ,
                             List<TransactionLocator> txLocators ) {
        this.blockLocator = blockLocator;
        this.headerLocator = headerLocator;
        this.txLocators = txLocators;
    }

    public FileRecordLocator getBlockLocator() {
        return blockLocator;
    }

    public FileRecordLocator getHeaderLocator() {
        return headerLocator;
    }

    public List<TransactionLocator> getTxLocators() {
        return txLocators;
    }
}
