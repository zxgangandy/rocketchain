package com.rocketchain.proto;

public class OrphanTransactionDescriptor {
    private Transaction transaction;

    public OrphanTransactionDescriptor(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
