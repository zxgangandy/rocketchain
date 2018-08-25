package com.rocketchain.chain;

import com.rocketchain.proto.*;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.index.KeyValueDatabase;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionPool {
    private final Logger logger = LoggerFactory.getLogger(TransactionPool.class);

    private BlockStorage storage ;
    private TransactionMagnet txMagnet ;

    public TransactionPool(BlockStorage storage, TransactionMagnet txMagnet) {
        this.storage = storage;
        this.txMagnet = txMagnet;
    }

    public List<Pair<Hash, Transaction>> getOldestTransactions(KeyValueDatabase db , int count)  {

        List<CStringPrefixed<Hash>> list = storage.getOldestTransactionHashes(db, count);

        return list.stream().map(item->{
            String createdAtString = item.getPrefix();
            Hash txHash = item.getData();

            TransactionPoolEntry txOption = storage.getTransactionFromPool(db, txHash);
            Pair<Hash, Transaction> pair;
            if (txOption != null) {
                pair = new MutablePair(txHash, txOption.getTransaction());
            } else { // TransactionTime exists, but no transaction exists matching the tx hash.
                // When two threads add transaction, remove transaction at the same time,
                // a garbage on the Transaction Time Index can exist. we need to remove them.
                storage.delTransactionTime(db, item);
                pair = null;
            }
            return pair;
        }).filter(item->Objects.nonNull(item)).collect(Collectors.toList());

    }
    /**
     * Add a transaction to disk pool.
     *
     * Assumption : The transaction was pointing to a transaction record location, which points to a transaction written
     * while the block was put into disk.
     * Caution : This method should be called with Blockchain.get.synchronized, because this method updates the spending
     * in-points of transactions.
     *
     * @param txHash The hash of the transaction to add.
     * @param transaction The transaction to add to the disk-pool.
     * @return true if the transaction was valid with all inputs connected. false otherwise. (ex> orphan transactions return false )
     */
    public void addTransactionToPool(KeyValueDatabase db , Hash txHash , Transaction transaction )  {
        // Step 01 : Check if the transaction exists in the disk-pool.
        if ( storage.getTransactionFromPool(db, txHash) != null ) {
            logger.info("A duplicate transaction in the pool was discarded. Hash : {}", txHash);
        } else {
            // Step 02 : Check if the transaction exists in a block in the best blockchain.
            TransactionDescriptor txDescOption = storage.getTransactionDescriptor(db, txHash);
            if (txDescOption != null ) {
                logger.info("A duplicate transaction in on a block was discarded. Hash : {}", txHash);
            } else {
                // Step 03 : CheckTransaction - check values in the transaction.

                // Step 04 : IsCoinBase - the transaction should not be a coinbase transaction. No coinbase
                // transaction is put into the disk-pool.

                // Step 05 : GetSerializeSize - Check the serialized size

                // Step 06 : GetSigOpCount - Check the script operation count.

                // Step 07 : IsStandard - Check if the transaction is a standard one.

                // Step 08 : Check the transaction fee.

                // Step 09 : Check for double-spends with existing transactions
                // First, check only without affecting the transaction database. If something is wrong such as double
                // spending issues, an exception is raised.
                //txMagnet.attachTransaction(txHash, transaction, checkOnly = true)

                // Step 09 : Add to the disk-pool
                txMagnet.attachTransaction(db, txHash, transaction, false, null, null, null);

                logger.trace("A transaction was put into pool. Hash : {}", txHash);
            }
        }
    }

    /**
     * Remove a transaction from the disk pool.
     * Called when a block is attached. We should not detach transaction inputs, because the inputs should still be attached.
     *
     * @param txHash The hash of the transaction to remove.
     */
    void removeTransactionFromPool(KeyValueDatabase db , Hash txHash)  {
        // Note : We should not touch the TransactionDescriptor.
        TransactionPoolEntry txOption  = storage.getTransactionFromPool(db, txHash);
        if (txOption != null ) {
            // BUGBUG : Need to remove these two records atomically
            storage.delTransactionTime(db, txOption.getCreatedAtNanos(), txHash);
            storage.delTransactionFromPool(db, txHash);
        }
    }
}
