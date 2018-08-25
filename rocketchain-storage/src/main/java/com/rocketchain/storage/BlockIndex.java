package com.rocketchain.storage;

import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;
import org.apache.commons.lang3.tuple.Pair;

public interface BlockIndex {
    /** Get a block by its hash.
     *
     * @param blockHash
     */
    Pair<BlockInfo, Block> getBlock(KeyValueDatabase db , Hash blockHash ) ;

    /** Get a transaction by its hash.
     *
     * @param transactionHash
     */
    Transaction getTransaction(KeyValueDatabase db, Hash transactionHash );
}
