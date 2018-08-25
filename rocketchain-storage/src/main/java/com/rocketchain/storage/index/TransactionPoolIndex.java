package com.rocketchain.storage.index;

import com.rocketchain.codec.HashCodec;
import com.rocketchain.codec.TransactionPoolEntryCodec;
import com.rocketchain.proto.CStringPrefixed;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.TransactionPoolEntry;
import com.rocketchain.storage.DB;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides index operations for disk-pool, which keeps transactions on-disk instead of mempool.
 * c.f. Orphan transactions are not stored in the disk-pool.
 */
public interface TransactionPoolIndex {
    final static String DUMMY_PREFIX_KEY = "0";

    //private val logger = LoggerFactory.getLogger(TransactionPoolIndex::class.java)
    default byte getTxPoolPrefix() {
        return DB.TRANSACTION_POOL;
    }

    ;

    /**
     * Put a transaction into the transaction pool.
     *
     * @param txHash               The hash of the transaction to add.
     * @param transactionPoolEntry The transaction to add.
     */
    default void putTransactionToPool(KeyValueDatabase db, Hash txHash, TransactionPoolEntry transactionPoolEntry) {
        //logger.trace(s"putTransactionDescriptor : ${txHash}")
        //println("putTransactionToPool ${txHash}")

        db.putPrefixedObject(new HashCodec(), new TransactionPoolEntryCodec(), getTxPoolPrefix(), DUMMY_PREFIX_KEY, txHash, transactionPoolEntry);

        // BUGBUG : Remove to improve performance
        //assert( getTransactionFromPool(db, txHash) != null)
    }

    /**
     * Get a transaction from the transaction pool.
     *
     * @param txHash The hash of the transaction to get.
     * @return The transaction which matches the given transaction hash.
     */
    default TransactionPoolEntry getTransactionFromPool(KeyValueDatabase db, Hash txHash) {
        //logger.trace(s"getTransactionFromPool : ${txHash}")

        return db.getPrefixedObject(new HashCodec(), new TransactionPoolEntryCodec(), getTxPoolPrefix(), DUMMY_PREFIX_KEY, txHash);
    }


    /**
     * Get all transactions in the pool.
     *
     * @return List of transactions in the pool. List of (transaction hash, transaction) pair.
     */
    default List<Pair<Hash, TransactionPoolEntry>> getTransactionsFromPool(KeyValueDatabase db) {
        ClosableIterator<Pair<CStringPrefixed<Hash>, TransactionPoolEntry>> iterator = db.seekPrefixedObject(new HashCodec(),
                new TransactionPoolEntryCodec(), getTxPoolPrefix(), DUMMY_PREFIX_KEY);
        try {
            List<Pair<Hash, TransactionPoolEntry>> list = new ArrayList<>();
            while (iterator.hasNext()) {
                MutablePair<Hash, TransactionPoolEntry> poolEntryPair = new MutablePair<>();
                Pair<CStringPrefixed<Hash>, TransactionPoolEntry> pair = iterator.next();
                poolEntryPair.setLeft(pair.getLeft().getData());
                poolEntryPair.setRight(pair.getRight());
                list.add(poolEntryPair);
            }

            return list;
        } finally {
            iterator.close();
        }
    }

    /**
     * Del a transaction from the pool.
     *
     * @param txHash The hash of the transaction to remove.
     */
    default void delTransactionFromPool(KeyValueDatabase db, Hash txHash) {
        //println("delTransactionToPool ${txHash}")
        //logger.trace(s"delTransactionFromPool : ${txHash}")

        db.delPrefixedObject(new HashCodec(), getTxPoolPrefix(), DUMMY_PREFIX_KEY, txHash);

        // BUGBUG : Remove to improve performance
        //assert( getTransactionFromPool(db, txHash) == null)
    }
}
