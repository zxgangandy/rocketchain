package com.rocketchain.proto;

public class OutPoint {

    private Hash transactionHash;
    private int outputIndex;

    public OutPoint(Hash transactionHash, int outputIndex) {
        this.transactionHash = transactionHash;
        this.outputIndex = outputIndex;
    }

    public Hash getTransactionHash() {
        return transactionHash;
    }

    public int getOutputIndex() {
        return outputIndex;
    }
}
