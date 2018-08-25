package com.rocketchain.chain;

import com.rocketchain.chain.transaction.NetEnv;
import com.rocketchain.chain.transaction.NetEnvFactory;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.Hash;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.index.TransactingKeyValueDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The block locator that can produce the list of block hashes that another node requires.
 * Another node first sends the list of locator hashes by using getLocatorHashes,
 * and constructs GetBlocks message to get inventories of the missing block hashes.
 * <p>
 * The receiver node finds out the common hash and produces a list of hashes sender needs.
 */
public class BlockLocator {
    private Logger logger = LoggerFactory.getLogger(BlockLocator.class);

    private Blockchain chain;

    public BlockLocator(Blockchain chain) {
        this.chain = chain;
    }

    /**
     * Get the summary of block hashes that this node has.
     * We will use these hashes to create the GetBlocks request.
     *
     * @return The list of locator hashes summarizing the blockchain.
     */
    public BlockLocatorHashes getLocatorHashes() {
        // BUGBUG : We need to be able to get locator hashes from a block that is not on the best blockchain.
        // Ex> When we get headers to get the best blockchain that other nodes have,
        // the headers we get might not be on the best blockchain of this node.

        NetEnv env = NetEnvFactory.get();

        List<Hash> listBuf = new ArrayList<>();
//        return chain.withTransaction {
//            transactingDB ->
//            long blockHeight = chain.getBestBlockHeight(); // The height of the block we are processing.
//            int addedHashes = 0; // The number of hashes added to the list.
//            int heightSteps = 1; // For each loop, how may heights do we jump?
////println(s"blockHeight=${blockHeight}")
//            while (blockHeight > 0) {
//                // Step 1 : Add 10 recent block hashes on the best blockchain.
//                listBuf.add(chain.getBlockHash(transactingDB, blockHeight));
//                addedHashes += 1;
//
//                // Step 2 : Exponentially move backwards to get a summarizing list of block hashes.
//                if (addedHashes >= 10) {
//                    // Multiply heightSteps.
//                    heightSteps = heightSteps << 1;
//                }
////println(s"heightSteps=${heightSteps}")
//
//                blockHeight -= heightSteps;
//            }
//
//            // Step 3 : Add the genesis block hash.
//            listBuf.add(env.getGenesisBlockHash());
//            new BlockLocatorHashes(listBuf);
//        }

        int addedHashes = 0;
        int heightSteps = 1;
        long blockHeight = chain.getBestBlockHeight();
        KeyValueDatabase db = chain.getDb();
        TransactingKeyValueDatabase transactingDB = db.transacting();
        while( blockHeight > 0 ) {
            // Step 1 : Add 10 recent block hashes on the best blockchain.
            listBuf.add( chain.getBlockHash(transactingDB, blockHeight) );
            addedHashes += 1;

            // Step 2 : Exponentially move backwards to get a summarizing list of block hashes.
            if (addedHashes >= 10) {
                // Multiply heightSteps.
                heightSteps = heightSteps << 1;
            }
//println(s"heightSteps=${heightSteps}")

            blockHeight -= heightSteps;
        }

        return null;
    }

    /**
     * Get a list of hashes from the hash that matches one in locatorHashes.
     * c.f. The locator Hashes is constructed by another node using getLocatorHashes, and sent to this node via GetBlocks message.
     * Recent hashes come first in the locatorHashes list.
     * <p>
     * We will use the result of this method to get the list of hashes to send to the requester of GetBlocks.
     *
     * @param locatorHashes The list of hashes to find from blockchain.
     *                      If any matches, we start constructing a list of hashes from it.
     * @param hashStop      While constructing the list of hashes, stop at this hash if the hash matches.
     */
    public List<Hash> getHashes(BlockLocatorHashes locatorHashes, Hash hashStop, int maxHashCount) {
        NetEnv env = NetEnvFactory.get();
        List<Hash> listBuf = new ArrayList<>();

        // TODO : Optimize : Can we remove the chain.synchronized, as putBlock is atomic? ( May require using a RocksDB snapshot )
//        return chain.withTransaction {
//            transactingDB ->
//                    // Step 1 : Find any matching hash from the list of locator hashes
//                    // Use hashes.view instead of hashes to stop calling chain.hasBlock when we hit any matching hash on the chain.
//                    //
//                    // scala> listOf(1,2,3)
//                    // res0: List<Int> = listOf(1, 2, 3)
//                    //
//                    // scala> (res0.view.map{ i=> println(s"$i"); i *2 }).head
//                    // 1
//                    // res9: Int = 2
//
//                    Hash matchedHashOption = locatorHashes.hashes.asSequence().filter {
//                hash ->
//                        val blockInfoOption = chain.getBlockInfo(transactingDB, hash);
//                // The block info exists, and the block is on the best block chain(nextBlockHash is defined)
//                blockInfoOption ?.nextBlockHash != null;
//            }.firstOrNull();
//
//            // Step 2 : Construct a list of hashes from the matching hash until the hashStop matches, or 500 hashes are constructed.
//            // If no hash matched, start from the genesis block.
//            Hash startingHash = matchedHashOption == null ? env.getGenesisBlockHash() : matchedHashOption;
//
//            BlockInfo blockInfo = chain.getBlockInfo(transactingDB, startingHash) ;
//            // The block should be on the best blockchain.
//            assert (blockInfo.getNextBlockHash() != null);
//
//            long bestBlockHeight = chain.getBestBlockHeight();
//            long blockHeight = blockInfo.getHeight();
//            if (blockHeight > bestBlockHeight) {
//                logger.error("Invalid block height. Block hash : {}, info : {}, best height : {}", startingHash, blockInfo, bestBlockHeight);
//                assert (false);
//            }
//
//            int addedHashes = 0;
//            Hash lastHash;
//
//            do {
//                // TODO : BUGBUG : Make sure that getBlockHash returns a block hash even though the blockchain has the header of the block only without any transaction data.
//                lastHash = chain.getBlockHash(transactingDB, blockHeight);
//                listBuf.add(lastHash);
//                addedHashes += 1;
//                blockHeight += 1;
//            } while (blockHeight <= bestBlockHeight && addedHashes < maxHashCount && lastHash != hashStop);
//
//            return listBuf;
//        }

        return null;
    }
}
