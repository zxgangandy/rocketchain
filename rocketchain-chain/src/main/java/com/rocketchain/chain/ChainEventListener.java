package com.rocketchain.chain;

import com.rocketchain.chain.transaction.ChainBlock;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;

/**
 * A listener that subscribes chain events such as transactions or removed transactions.
 */

public interface ChainEventListener {

    /**
     * Called whenever a transaction comes into a block or the disk-pool.
     *
     * @param transaction The newly found transaction.
     */
    void onNewTransaction(KeyValueDatabase db, Hash transactionHash, Transaction transaction, ChainBlock chainBlock, int transactionIndex);

    /**
     * Called whenever a transaction is removed from the disk-pool without being added to a block.
     * This also means the transaction does not exist in any block, as the disk-pool has transactions
     * that are not in any block in the best block chain.
     *
     * @param transaction The transaction removed from the disk-pool.
     */
    void onRemoveTransaction(KeyValueDatabase db, Hash transactionHash, Transaction transaction);

}
