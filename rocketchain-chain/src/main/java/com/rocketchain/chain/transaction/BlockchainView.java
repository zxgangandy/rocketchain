package com.rocketchain.chain.transaction;

import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;

import java.util.Iterator;

/** The read-only view of the best blockchain.
 */
public interface BlockchainView  extends CoinsView{

    /** Return an iterator that iterates each ChainBlock.
     *
     * Used by importaddress RPC to rescan blockchain to put related transactions and transaction outputs into the wallet database.
     *
     * @param height Specifies where we start the iteration. The height 0 means the genesis block.
     * @return The iterator that iterates each ChainBlock.
     */
    Iterator<ChainBlock> getIterator(KeyValueDatabase db , long height);

    /** Return the block height of the best block.
     *
     * Used by RPCs to get the number of confirmations since a specific block.
     *
     * @return The best block height.
     */
    long getBestBlockHeight() ;

    /** Return a transaction that matches the given transaction hash.
     *
     * Used by listtransaction RPC
     *
     * @param transactionHash The transaction hash to search.
     * @return Some(transaction) if the transaction that matches the hash was found. None otherwise.
     */
    Transaction getTransaction(KeyValueDatabase db , Hash transactionHash ) ;
}
