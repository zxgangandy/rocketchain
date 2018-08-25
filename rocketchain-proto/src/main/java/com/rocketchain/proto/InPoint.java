package com.rocketchain.proto;


public class InPoint {
    private Hash transactionHash;
    private int inputIndex;

    /**
     * An in point points to an input in a transaction.
     *
     * @param transactionHash The hash of the transaction that has the output.
     * @param inputIndex      The index of the input. The index starts from 0. Ex> The first input of a transaction has index 0.
     */
    public InPoint(Hash transactionHash, int inputIndex) {
        this.transactionHash = transactionHash;
        this.inputIndex = inputIndex;
    }

    public Hash getTransactionHash() {
        return transactionHash;
    }

    public int getInputIndex() {
        return inputIndex;
    }
}
