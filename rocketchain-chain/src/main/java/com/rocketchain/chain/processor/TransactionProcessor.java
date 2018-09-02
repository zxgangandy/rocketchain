package com.rocketchain.chain.processor;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;

/** Processes a received transaction.
 *
 */
public class TransactionProcessor {

    private static Blockchain chain ;

    public TransactionProcessor(Blockchain chain) {
        this.chain = chain;
    }

    public TransactionProcessor() {
        this(Blockchain.get());
    }

    /** Get a transaction either from a block or from the transaction disk-pool.
     * getTransaction does not return orphan transactions.
     *
     * @param txHash The hash of the transaction to get.
     * @return Some(transaction) if the transaction was found; None otherwise.
     */
    public Transaction getTransaction(KeyValueDatabase db , Hash txHash )  {
        return chain.getTransaction(db, txHash);
    }
}
