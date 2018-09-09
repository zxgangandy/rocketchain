package com.rocketchain.chain;

import com.rocketchain.chain.transaction.CoinAmount;
import com.rocketchain.proto.Transaction;

public class TransactionWithFee {
    private Transaction transaction ;
    private CoinAmount fee;

    public TransactionWithFee(Transaction transaction, CoinAmount fee) {
        this.transaction = transaction;
        this.fee = fee;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public CoinAmount getFee() {
        return fee;
    }
}
