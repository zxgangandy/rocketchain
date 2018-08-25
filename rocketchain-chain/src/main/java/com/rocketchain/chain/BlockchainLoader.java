package com.rocketchain.chain;

import com.rocketchain.proto.Hash;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.index.KeyValueDatabase;

public class BlockchainLoader {

    private KeyValueDatabase db ;
    private Blockchain chain;
    private BlockStorage storage ;

    public BlockchainLoader(KeyValueDatabase db, Blockchain chain, BlockStorage storage) {
        this.db = db;
        this.chain = chain;
        this.storage = storage;
    }

    public void load()  {
        Hash bestBlockHashOption = storage.getBestBlockHash(db);
        if (bestBlockHashOption != null) {
            // Set the best block descriptor.
            chain.setTheBestBlock(storage.getBlockInfo(db, bestBlockHashOption));
        } else {
            // We don't have the best block hash yet.
            // This means that we did not put the genesis block yet.
            // On the CLI layer, while initializing all layers, the genesis block will be put, so we do nothing here.
        }
    }
}
