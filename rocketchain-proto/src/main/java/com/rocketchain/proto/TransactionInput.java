package com.rocketchain.proto;

public class TransactionInput {
    private Hash outputTransactionHash;
    private long outputIndex;

    public TransactionInput(Hash outputTransactionHash, long outputIndex) {
        this.outputTransactionHash = outputTransactionHash;
        this.outputIndex = outputIndex;
    }

    public void setOutputTransactionHash(Hash outputTransactionHash) {
        this.outputTransactionHash = outputTransactionHash;
    }

    public void setOutputIndex(long outputIndex) {
        this.outputIndex = outputIndex;
    }

    public Hash getOutputTransactionHash() {
        return outputTransactionHash;
    }

    public long getOutputIndex() {
        return outputIndex;
    }


    /**
     * See if the transaction input data represents the generation transaction input.
     * <p>
     * Generation transaction's UTXO hash has all bits set to zero,
     * and its UTXO index has all bits set to one.
     *
     * @return true if the give transaction input is the generation transaction. false otherwise.
     */
    public boolean isCoinBaseInput() {
        //println(s"${txInput.outputIndex}")
        return outputTransactionHash.isAllZero() && (outputIndex == 0xFFFFFFFFL);
    }


    public OutPoint getOutPoint() {
        return new OutPoint(outputTransactionHash, (int) outputIndex);
    }

    @Override
    public String toString() {
        return "TransactionInput{" +
                "outputTransactionHash=" + outputTransactionHash +
                ", outputIndex=" + outputIndex +
                '}';
    }
}
