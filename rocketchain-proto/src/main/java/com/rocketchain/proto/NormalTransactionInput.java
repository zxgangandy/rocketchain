package com.rocketchain.proto;

public class NormalTransactionInput extends TransactionInput {

    private UnlockingScript unlockingScript;
    private long sequenceNumber;

    public NormalTransactionInput(Hash outputTransactionHash,
                           long outputIndex,
                           UnlockingScript unlockingScript,
                           long sequenceNumber) {
        super(outputTransactionHash, outputIndex);

        this.unlockingScript = unlockingScript;
        this.sequenceNumber = sequenceNumber;
    }

    public NormalTransactionInput(Hash outputTransactionHash, long outputIndex) {
        super(outputTransactionHash, outputIndex);
    }

    public UnlockingScript getUnlockingScript() {
        return unlockingScript;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
