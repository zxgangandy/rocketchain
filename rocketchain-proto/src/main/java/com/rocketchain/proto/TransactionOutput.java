package com.rocketchain.proto;

public class TransactionOutput {
    private Long value;
    private LockingScript lockingScript;

    public TransactionOutput(Long value, LockingScript lockingScript) {
        this.value = value;
        this.lockingScript = lockingScript;
    }

    public Long getValue() {
        return value;
    }

    public LockingScript getLockingScript() {
        return lockingScript;
    }

    @Override
    public String toString() {
        return "TransactionOutput{" +
                "value=" + value +
                ", lockingScript=" + lockingScript +
                '}';
    }
}
