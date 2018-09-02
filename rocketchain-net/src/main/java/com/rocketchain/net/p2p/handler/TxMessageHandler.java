package com.rocketchain.net.p2p.handler;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.chain.processor.TransactionProcessor;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ChainException;
import com.rocketchain.utils.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The message handler for Tx message.
 */
public class TxMessageHandler {
    private Logger logger = LoggerFactory.getLogger(TxMessageHandler.class);

    /**
     * Handle Transaction message.
     *
     * @param context     The context where handlers handling different messages for a peer can use to store state data.
     * @param transaction The Transaction message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle(MessageHandlerContext context, Transaction transaction) {
        KeyValueDatabase db = Blockchain.get().getDb();

        Hash transactionHash = HashUtil.hashTransaction(transaction);
        logger.trace("<P2P> Received a transaction. Hash : {}", transactionHash);

        // BUGBUG : Do not process the message during initial block download.
        //
        //if ( ! Node.get().isInitialBlockDownload() ) {
        {
            // TODO : Step 0 : Add the inventory as a known inventory to the node that sent the "tx" message.
            try {
                if (new TransactionProcessor().exists(db, transactionHash)) {
                    logger.trace("The transaction already exists. ${transaction}");
                } else {

                    // Try to put the transaction into the disk-pool
                    new TransactionProcessor().putTransaction(db, transactionHash, transaction);

                    // Yes! the transaction was put into the disk-pool.
                    // Step 2 : Recursively check if any orphan transaction depends on this transaction.
                    // Also delete the newly accepted transactions from indexes for orphan transactions.
                    List<Hash> acceptedChildren = new TransactionProcessor().acceptChildren(db, transactionHash);
          /*
                  // Step 3 : Relay the transaction as an inventory
                  val invMessage = InvFactory.createTransactionInventories(transactionHash :: acceptedChildren)
                  context.communicator.sendToAll(invMessage)
                  logger.trace(s"Propagating inventories for the newly accepted transactions. ${invMessage}")
          */
                }
            } catch (ChainException e) {
                if (e.getCode() == ErrorCode.ParentTransactionNotFound) {
                    // A transaction pointed by an input of the transaction does not exist. add it as an orphan.
                    new TransactionProcessor().putOrphan(db, transactionHash, transaction);
                    logger.info("An orphan transaction was received. Hash : ${transactionHash}, Transaction : ${transaction}");
                } else if (e.getCode() == ErrorCode.TransactionOutputAlreadySpent) {
                    logger.trace("A double spending transaction was received. Hash : ${transactionHash}, Transaction : ${transaction}");
                }
            }
        }


 /*
    pfrom->AddInventoryKnown(tx inventory)

    if (tx.AcceptToMemoryPool(true, &fMissingInputs)) // Not an orphan
    {
        // Step 1 : Notify the transaction, check if the transaction should be stored in the wallet.
        SyncWithWallets

        // Step 2 : Relay the transaction as an inventory
        RelayMessage
          - RelayInventory // For each connected node, relay the transaction inventory.

        // Step 3 : Recursively check if any orphan transaction depends on this transaction.
        Loop newTx := each transaction newly added
          Loop orphanTx := for each transaction that depends on the newTx
            if (orphanTx.AcceptToMemoryPool(true)) { // Not an orphan anymore
              add the tx to the newly added transactions list.

        // Step 4 : For each orphan transaction that has all inputs connected, remove from the orphan transaction.
        Loop newTx := each transaction newly added
          EraseOrphanTx(hash);
            - Remove the orphan transaction both from mapOrphanTransactions and mapOrphanTransactionsByPrev.
    }
    else if (fMissingInputs) // An orphan
    {
        // Add the transaction as an orphan transaction.
        AddOrphanTx(vMsg);
        - Add the orphan transaction to mapOrphanTransactions and mapOrphanTransactionsByPrev.
    }
 */
    }
}
