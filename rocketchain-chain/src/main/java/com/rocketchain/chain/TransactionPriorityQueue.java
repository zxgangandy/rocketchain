package com.rocketchain.chain;

import com.rocketchain.chain.transaction.CoinsView;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;

import java.util.PriorityQueue;

public class TransactionPriorityQueue {
    private CoinsView coinsView;

    public TransactionPriorityQueue(CoinsView coinsView) {
        this.coinsView = coinsView;
    }

    PriorityQueue<TransactionWithFee> queue = new PriorityQueue(new DescendingTransactionFeeComparator());

    public void enqueue(KeyValueDatabase db, Transaction tx) {
        queue.add(new TransactionWithFee(tx, TransactionFeeCalculator.fee(db, coinsView, tx)));
    }

    public Transaction dequeue() {
        TransactionWithFee txWithFee = queue.poll();
        return txWithFee.getTransaction();
    }
}
