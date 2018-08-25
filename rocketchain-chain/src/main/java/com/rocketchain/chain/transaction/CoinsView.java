package com.rocketchain.chain.transaction;

import com.rocketchain.proto.OutPoint;
import com.rocketchain.proto.TransactionOutput;
import com.rocketchain.storage.index.KeyValueDatabase;

/**
 * The read-only view of the coins in the best blockchain.
 */
public interface CoinsView {
    /**
     * Return a transaction output specified by a give out point.
     *
     * @param outPoint The outpoint that points to the transaction output.
     * @return The transaction output we found.
     */
    TransactionOutput getTransactionOutput(KeyValueDatabase db, OutPoint outPoint);
}
