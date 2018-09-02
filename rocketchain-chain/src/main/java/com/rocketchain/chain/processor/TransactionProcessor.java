package com.rocketchain.chain.processor;

import com.google.common.collect.Lists;
import com.rocketchain.chain.Blockchain;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ChainException;
import com.rocketchain.utils.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Processes a received transaction.
 */
public class TransactionProcessor {

    private Blockchain chain;

    public TransactionProcessor(Blockchain chain) {
        this.chain = chain;
    }

    public TransactionProcessor() {
        this(Blockchain.get());
    }


    /**
     * See if a transaction exists. Checks orphan transactions as well.
     * naming rule : 'exists' checks orphan transactions as well, whereas hasNonOrphan does not.
     *
     * @param txHash The hash of the transaction to check the existence.
     * @return true if the transaction was found; None otherwise.
     */
    public  boolean exists(KeyValueDatabase db, Hash txHash) {
        return chain.hasTransaction(db, txHash) || chain.getTxOrphanage().hasOrphan(db, txHash);
    }

    /**
     * Get a transaction either from a block or from the transaction disk-pool.
     * getTransaction does not return orphan transactions.
     *
     * @param txHash The hash of the transaction to get.
     * @return Some(transaction) if the transaction was found; None otherwise.
     */
    public Transaction getTransaction(KeyValueDatabase db, Hash txHash) {
        return chain.getTransaction(db, txHash);
    }


    /**
     * Add a transaction to disk pool.
     * <p>
     * Assumption : The transaction was pointing to a transaction record location, which points to a transaction written while the block was put into disk.
     *
     * @param txHash      The hash of the transaction to add.
     * @param transaction The transaction to add to the disk-pool.
     * @return true if the transaction was valid with all inputs connected. false otherwise. (ex> orphan transactions return false )
     */
    public  void putTransaction(KeyValueDatabase db, Hash txHash, Transaction transaction) {
//    synchronized { // To prevent double spends, we need to synchronize transactions to put.
        // TODO : Need to check if the validity of the transation?
//      chain.withTransaction { implicit transactingDB =>
//        chain.putTransaction(txHash, transaction)(transactingDB)
//      }
//    }
        // TODO : BUGBUG : Change to record level locking with atomic update.
        chain.putTransaction(db, txHash, transaction);
    }

    /**
     * Add an orphan transaction.
     *
     * @param txHash      The hash of the orphan transaction
     * @param transaction The orphan transaction.
     */
    public  void putOrphan(KeyValueDatabase db, Hash txHash, Transaction transaction) {
        chain.getTxOrphanage().putOrphan(db, txHash, transaction);
    }

    /**
     * Recursively accepts children of the given parent.
     *
     * @param initialParentTxHash The hash of the parent transaction that an orphan might depend on.
     * @return The list of hashes of accepted children transactions.
     */
    public List<Hash> acceptChildren(KeyValueDatabase db, Hash initialParentTxHash) {
        synchronized (this) { // do not allow two threads run acceptChildren at the same time.
            List<Hash> acceptedChildren = Lists.newArrayList();

            int i = -1;
            do {
                Hash parentTxHash;

                if (acceptedChildren.size() == 0) {
                    parentTxHash = initialParentTxHash;
                } else {
                    parentTxHash = acceptedChildren.get(i);
                }

                List<Hash> dependentChildren = chain.getTxOrphanage().getOrphansDependingOn(db, parentTxHash);

                dependentChildren.stream().forEach(dependentChildHash -> {
                    Transaction dependentChild = chain.getTxOrphanage().getOrphan(db, dependentChildHash);
                    if (dependentChild != null) {
                        try {
                            //println(s"trying to accept a child. ${dependentChildHash}")
                            // Try to add to the transaction pool.
                            putTransaction(db, dependentChildHash, dependentChild);
                            // add the hash to the acceptedChildren so that we can process children of the acceptedChildren as well.
                            acceptedChildren.add(dependentChildHash);
                            // del the orphan
                            chain.getTxOrphanage().delOrphan(db, dependentChildHash);

                            //println(s"accepted a child. ${dependentChildHash}")

                        } catch (ChainException e) {
                            if (e.getCode() == ErrorCode.TransactionOutputAlreadySpent) {
                                // The orphan turned out to be a conflicting transaction.
                                // do nothing.
                                // TODO : Add a test case.
                            } else if (e.getCode() == ErrorCode.ParentTransactionNotFound) {
                                // The transaction depends on another parent transaction.
                                // do nothing. Still an orphan transaction.
                                // TODO : Add a test case.
                            } else {
                                throw e;
                            }
                        }
                    } else {
                        // The orphan tranasction was already deleted. nothing to do.
                    }
                });

                chain.getTxOrphanage().removeDependenciesOn(db, parentTxHash);
                //}
                i += 1;
            } while (i < acceptedChildren.size());

            // Remove duplicate by converting to a set, and return as a list.
            return acceptedChildren.stream()
                    .collect(Collectors.toSet())
                    .stream()
                    .collect(Collectors.toList());
        }
    }

}
