package com.rocketchain.chain;

import com.rocketchain.proto.Hash;
import com.rocketchain.proto.OrphanTransactionDescriptor;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.index.KeyValueDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionOrphanage {
    private BlockStorage storage;

    public TransactionOrphanage(BlockStorage storage) {
        this.storage = storage;
    }

    public BlockStorage getStorage() {
        return storage;
    }

    /**
     * Remove a transaction from the indexes maintaining the orphans.
     *
     * @param orphanTxHash The hash of the accepted orphan transaction to remove.
     */
    public void delOrphan(KeyValueDatabase db, Hash orphanTxHash) {
        storage.delOrphanTransaction(db, orphanTxHash);
    }

    /**
     * Add an orphan transaction.
     *
     * @param txHash      The hash of the orphan transaction
     * @param transaction The orphan transaction.
     */
    public void putOrphan(KeyValueDatabase db, Hash txHash, Transaction transaction) {
        // TODO : BUGBUG : Need a recovery mechanism for the crash during the excution of this method.

        // Step 1 : Add the orphan transaction itself.
        storage.putOrphanTransaction(db, txHash, new OrphanTransactionDescriptor(transaction));

        // Step 2 : Find all inputs that depend on a missing parent transaction.
        List<Hash> hashes = transaction.getInputs().stream()
                .map(input -> input.getOutputTransactionHash())
                .filter(hash -> !storage.hasTransaction(db, hash))
                .collect(Collectors.toList());

        // Step 3 : Add the orphan transaction indexed by the missing parent transactions.
        hashes.stream().forEach(missingTxHash -> {
            storage.addOrphanTransactionByParent(db, missingTxHash, txHash);
        });
    }


    /**
     * Get the orphan transaction
     *
     * @param txHash The hash of the orphan transaction to get.
     * @return Some(transaction) if the orphan exists; None otherwise.
     */
    public Transaction getOrphan(KeyValueDatabase db , Hash txHash) {
        OrphanTransactionDescriptor descriptor = storage.getOrphanTransaction(db, txHash);
        return descriptor == null ? null : descriptor.getTransaction();
    }

    /**
     * Check if the orphan exists.
     *
     * @param txHash The hash of the orphan to check the existence.
     * @return true if it exists; false otherwise.
     */
    public boolean hasOrphan(KeyValueDatabase db, Hash txHash) {
        // TODO : OPTIMIZE : Just check if the orphan exists without decoding the block data.
        return storage.getOrphanTransaction(db, txHash) != null;
    }


    /**
     * Get the list of orphan transaction hashes depending the given block.
     *
     * @param blockHash The block that orphans are depending on.
     * @return The list of orphan block hashes depending the given block.
     */
    public List<Hash> getOrphansDependingOn(KeyValueDatabase db, Hash blockHash) {
        return storage.getOrphanTransactionsByParent(db, blockHash);
    }

    /**
     * Remove the dependent transactions on a given hash.
     *
     * @param blockHash The mapping from the block hash to the hashes of transactions depending on it is removed.
     */
    public void removeDependenciesOn(KeyValueDatabase db, Hash blockHash) {
        storage.delOrphanTransactionsByParent(db, blockHash);
    }
}
