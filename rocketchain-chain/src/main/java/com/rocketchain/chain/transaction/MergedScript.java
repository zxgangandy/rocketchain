package com.rocketchain.chain.transaction;

import com.rocketchain.proto.LockingScript;
import com.rocketchain.proto.Transaction;
import com.rocketchain.proto.UnlockingScript;

public class MergedScript {

    private Transaction transaction;
    private int inputIndex;
    private UnlockingScript unlockingScript;
    private LockingScript lockingScript;

    public MergedScript(Transaction transaction, int inputIndex, UnlockingScript unlockingScript, LockingScript lockingScript) {
        this.transaction = transaction;
        this.inputIndex = inputIndex;
        this.unlockingScript = unlockingScript;
        this.lockingScript = lockingScript;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public UnlockingScript getUnlockingScript() {
        return unlockingScript;
    }

    public LockingScript getLockingScript() {
        return lockingScript;
    }
}
