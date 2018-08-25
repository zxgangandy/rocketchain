package com.rocketchain.proto;

import java.util.List;

public class TransactionPoolEntry {
    private Transaction transaction ;
    private List<InPoint> outputsSpentBy ;
    private long createdAtNanos ;

    /**
     * An entry in the transaction pool. Transactions that are not kept in any block on the best blockchain are kept in the transaction pool.
     *
     * @param transaction The transaction in the transaction pool.
     * @param outputsSpentBy List of transaction inputs that spends outputs of the transaction.
     *                       For each element of the list, it is Some(inPoint) if an output was spent, None otherwise.
     */
    public TransactionPoolEntry(Transaction transaction, List<InPoint> outputsSpentBy, long createdAtNanos) {
        this.transaction = transaction;
        this.outputsSpentBy = outputsSpentBy;
        this.createdAtNanos = createdAtNanos;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public List<InPoint> getOutputsSpentBy() {
        return outputsSpentBy;
    }

    public long getCreatedAtNanos() {
        return createdAtNanos;
    }

    public void setOutputsSpentBy(List<InPoint> outputsSpentBy) {
        this.outputsSpentBy = outputsSpentBy;
    }
}
