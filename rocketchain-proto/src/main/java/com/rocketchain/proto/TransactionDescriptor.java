package com.rocketchain.proto;

import java.util.List;

public class TransactionDescriptor {
    private FileRecordLocator transactionLocator;
    private long blockHeight;
    List<InPoint> outputsSpentBy ;

    public TransactionDescriptor(FileRecordLocator transactionLocator, long blockHeight, List<InPoint> outputsSpentBy) {
        this.transactionLocator = transactionLocator;
        this.blockHeight = blockHeight;
        this.outputsSpentBy = outputsSpentBy;
    }

    public void setOutputsSpentBy(List<InPoint> outputsSpentBy) {
        this.outputsSpentBy = outputsSpentBy;
    }

    public FileRecordLocator getTransactionLocator() {
        return transactionLocator;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public List<InPoint> getOutputsSpentBy() {
        return outputsSpentBy;
    }
}
