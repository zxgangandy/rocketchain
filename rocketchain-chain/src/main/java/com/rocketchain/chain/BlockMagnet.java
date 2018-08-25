package com.rocketchain.chain;

import com.google.common.collect.Lists;
import com.rocketchain.chain.processor.BlockProcessor;
import com.rocketchain.chain.transaction.ChainBlock;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.*;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.BlockWriter;
import com.rocketchain.storage.TransactionLocator;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ChainException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class BlockMagnet {

    private Logger logger = LoggerFactory.getLogger(BlockMagnet.class);

    private BlockStorage storage;
    private TransactionPool txPool;
    private TransactionMagnet txMagnet;

    public BlockMagnet(BlockStorage storage, TransactionPool txPool, TransactionMagnet txMagnet) {
        this.storage = storage;
        this.txPool = txPool;
        this.txMagnet = txMagnet;
    }

    /**
     * Attach a block to the best blockchain.
     *
     * @param block The block to attach
     */
    public void attachBlock(KeyValueDatabase db, BlockInfo blockInfo, Block block) {
        Hash blockHash = HashUtil.hashBlockHeader(block.getHeader());
        // Check if the block is valid.
        BlockProcessor.get().validateBlock(block);

        assert (blockInfo.getBlockHeader() == block.getHeader());

        List<TransactionLocator> txLocators = BlockWriter.getTxLocators(blockInfo.getBlockLocatorOption(), block);

        // TODO : BUGBUG : Optimize : length on List is slow.
        assert (txLocators.size() == block.getTransactions().size());

        // TODO : BUGBUG : P0 : Need to check with a temporary transaction pool.
/*
    // Before attaching a block, check if we can attach each transaction first without affecting the transaction database.
    // If any error such as double spending is detected, an exception is raised.
    for ( ( txLocator : TransactionLocator, transaction: Transaction) <- (txLocators zip block.transactions)) {
      val transactionHash = transaction.hash
      txMagnet.attachTransaction(transactionHash, transaction, Some(txLocator.txLocator), checkOnly = true)
    }
*/
        ChainBlock chainBlockOption = new ChainBlock(blockInfo.getHeight(), block);
        long blockHeightOption = blockInfo.getHeight();
        int transactionIndex = -1;
        for (Transaction transaction : block.getTransactions()) {
            transactionIndex += 1;

            Hash transactionHash = HashUtil.hashTransaction(transaction);
            TransactionLocator txLocator = txLocators.get(transactionIndex);

            txMagnet.attachTransaction(db, transactionHash, transaction, false/*checkOnly*/,
                    txLocator.getTxLocator(), chainBlockOption, transactionIndex);

            // Step 5 : Remove the transaction from the disk pool.
            txPool.removeTransactionFromPool(db, transactionHash);
            //logger.trace(s"<Attach Block> Removed transaction from pool : ${transactionHash}")
        }

        // TODO : Check if the generation transaction's output amount is less than or equal to the reward + sum of fees for all transactions in the block.

        // Put the index for block hash by height
        storage.putBlockHashByHeight(db, blockInfo.getHeight(), blockHash);

        Hash prevBlockHash = blockInfo.getBlockHeader().getHashPrevBlock();
        if (prevBlockHash.isAllZero()) {
            // the genesis block. do nothing. genesis blocks are not reorganized, but can be attached when the block is put for the first time.
        } else {
            // Link the next block hash.
            storage.updateNextBlockHash(db, prevBlockHash, blockHash);
        }
    }


    /**
     * Reorganize blocks.
     * This method is called when the new best block is not based on the original best block.
     *
     * @param originalBestBlock The original best block before the new best one was found.
     * @param newBestBlock      The new best block, which has greater chain work than the original best block.
     */
    public void reorganize(KeyValueDatabase db, BlockInfo originalBestBlock, BlockInfo newBestBlock) {
        // TODO : BUGBUG : Need to think about RocksDB transactions.

        assert (originalBestBlock.getChainWork() < newBestBlock.getChainWork());

        List<Block> detachedBlocks = Lists.newArrayList();

        // Step 1 : Find the common block(pfork) between the current blockchain(pindexBest) and the new longer blockchain.
        BlockHeader commonBlockHeader = findCommonBlock(db, originalBestBlock, newBestBlock);

        // TODO : Call chainEventListener : onNewBlock, onRemoveBlock

        // Step 2 : Detach blocks after the common block to originalBestBlock, which is the tip of the current best blockchain.
        // TODO : BUGBUG : First need to get the list of detached blocks first, and then add transactions in the detached blocks into the transaction pool.
        Pair<BlockInfo, Block> pair = storage.getBlock(db, HashUtil.hashBlockHeader(originalBestBlock.getBlockHeader()));
        BlockInfo bestBlockInfo = pair.getKey();
        Block bestBlock = pair.getValue();

        assert (bestBlockInfo == originalBestBlock);
        detachBlocksAfter(db, commonBlockHeader, bestBlockInfo, bestBlock, detachedBlocks);


        // Step 3 : Attach blocks after the common block to the newBestBlock.
        attachBlocksAfter(db, commonBlockHeader, newBestBlock);

        // Step 4 : Move the transaction from the detached blocks into the transaction pool.
        // Note 1 : Some transactions might not be able to put into the transaction pool, because of double spending the UTXO spent by transactions on newly attached blocks.
        // Note 2 : Some transactions might not be able to put into the transaction pool, because it depends on the above double spending transactions.
        // TODO : Do we really need to reset the transaction record locator? How do we recover it when we attach the transaction again?

        detachedBlocks.stream().forEach(block -> {
            block.getTransactions().stream().forEach(transaction -> {
                Hash transactionHash = HashUtil.hashTransaction(transaction);
                if (transaction.getInputs().get(0).isCoinBaseInput()) {
                    // Do nothing
                } else {
                    try {
                        if (storage.hasTransaction(db, transactionHash)) {
                            // The transaction already exists.
                            // This can happen, as the newly attached block might have the same transaction. Do nothing.
                        } else {
                            txPool.addTransactionToPool(db, transactionHash, transaction);
                        }
                    } catch (ChainException e) {

                    }
                }
            });
        });

    /*



      // Step 2 : Get the list of blocks to disconnect from the common block to the tip of the current blockchain

      // Step 3 : Get the list of blocks to connect from the common block to the longer blockchain.

      // Step 4 : Reorder the list of blocks to connect so that the blocks with lower height come first.

      // Step 5 : Disconnect blocks from the current (shorter) blockchain. (order : newest to oldest)
      LOOP block := For each block to disconnect
          // Step 5.1 : Read each block, and disconnect each block.
          block.ReadFromDisk(pindex)
          block.DisconnectBlock(txdb, pindex)
              1. Mark all outputs spent by all inputs of all transactions in the block as unspent.
              LOOP tx := For each transaction in the block
                  1.1. Mark outputs pointed by the inputs of the transaction unspent.
                  tx.DisconnectInputs
                      - LOOP input := For each input in the transaction
                          - Get the transaction pointed by the input
                          - On disk, Mark the output point by the input as spent
              2. On disk, disconnect from the previous block
                  (previous block.next = null)

          // Step 5.2 : Prepare transactions to add back to the mempool.
      }

      // Step 6 : Connect blocks from the longer blockchain to the current blockchain. (order : oldest to newest)
      LOOP block := For each block to connect
          // kangmo : comment - Step 6.1 : Read block, connect the block to the current blockchain, which does not have the disconnected blocks.
          block.ReadFromDisk(pindex)
          block.ConnectBlock(txdb, pindex)
              - 1. Do preliminary checks for a block
              pblock->CheckBlock()

              - 2. Prepare a queue for database changes marking outputs spent by all inputs of all transactions in the block.
              map<uint256, CTxIndex> mapQueuedChanges;

              - 3. Populate mapQueuedChanges with transaction outputs marking which transactions are spending each of the outputs.
              LOOP tx := For each transaction in the block
                  - 3.1 Mark transaction outputs pointed by inputs of this transaction spent.
                  IF not coinbase transaction
                      CTransaction::ConnectInputs( .. & mapQueuedChanges .. )
                          LOOP input := for each input in the transaction
                              // 1. read CTxIndex from disk if not read yet.
                              // 2. read the transaction that the outpoint points from disk if not read yet.
                              // 3. Increase DoS score if an invalid output index was found in a transaction input.
                              // 4. check coinbase maturity for outpoints spent by a transaction.
                              // 5. Skip ECDSA signature verification when connecting blocks (fBlock=true) during initial download
                              // 6. check double spends for each OutPoint of each transaction input in a transaction.
                              // 7. check value range of each input and sum of inputs.
                              // 8. for the transaction output pointed by the input, mark this transaction as the spending transaction of the output.

                          // check if the sum of input values is greater than or equal to the sum of outputs.
                          // make sure if the fee is not negative.
                          // check the minimum transaction fee for each transaction.
                  // Add UTXO : set all outputs are unspent for the newly connected transaction.

              - 4. For each items in mapQueuedChanges, write to disk.
              - 5. Check if the generation transaction's output amount is less than or equal to the reward + sum of fees for all transactions in the block.
              - 6. On disk, connect the block from the previous block. (previous block.next = this block)

              - 7. For each transaction, sync with wallet.
              LOOP tx := For each transaction in the block
                  SyncWithWallets
                      - For each registered wallet
                           pwallet->AddToWalletIfInvolvingMe
          // Step 6.2 : Prepare transactions to remove from the mempool
      }

      // Step 7 : Write the hash of the tip block on the best blockchain, commit the db transaction.

      // Step 8 : Set the next block pointer for each connected block. Also, set next block pointer to null for each disconnected block.
      // Note : next pointers of in-memory block index nodes are modified after the on-disk transaction commits the on-disk version of the next pointers.

      // Step 9 : add transactions in the disconnected blocks to the mempool.

      // Step 10 : Remove transactions in the connected blocks from the mempool.
*/
    }

    /**
     * Attach blocks to the best blockchain. Oldest blocks are attached first. (Attach order : Oldest -> Newest )
     *
     * @param beforeFirstHeader Blocks after this block are attached to the best blockchain.
     * @param last              The last block in the best blockchain.
     */
    private void attachBlocksAfter(KeyValueDatabase db, BlockHeader beforeFirstHeader, BlockInfo last) {
        // Blocks NOT in the best blockchain does not have the BlockInfo.nextBlockHash.
        // We need to track from the last back to beforeFirst, and reverse the list.
        List<BlockInfo> blockInfos = Lists.newArrayList();
        collectBlockInfos(db, blockInfos, beforeFirstHeader, last);

        Hash prevBlockHash = HashUtil.hashBlockHeader(beforeFirstHeader);
        // The previous block of the first block in the list buffer should be the beforeFirst block.
        assert (prevBlockHash == blockInfos.get(0).getBlockHeader().getHashPrevBlock());
        // The last block info in the list buffer should match the last block info passed to this method.
        assert (last == blockInfos.get(blockInfos.size() - 1));

        for (BlockInfo blockInfo : blockInfos) {
            Hash blockHash = HashUtil.hashBlockHeader(blockInfo.getBlockHeader());
            // Get the block
            Pair<BlockInfo, Block> pair = storage.getBlock(db, blockHash);
            ;
            BlockInfo readBlockInfo = pair.getKey();
            Block readBlock = pair.getValue();
            assert (readBlockInfo == blockInfo);

            attachBlock(db, readBlockInfo, readBlock);

            prevBlockHash = blockHash;
        }

        // The last block should not have any next block.
        assert (last.getNextBlockHash() == null);
    }


    /**
     * collect BlockInfos from the last to the next block of the beforeFirst.
     * The collected BlockInfos are kept in blockInfos List in ascending order. (Order : Oldest -> Newest)
     *
     * @param blockInfos        The list buffer to keep the block info.
     * @param beforeFirstHeader Blocks after this block are collected.
     * @param last              The last block to collect.
     */

    private void collectBlockInfos(KeyValueDatabase db, List<BlockInfo> blockInfos, BlockHeader beforeFirstHeader, BlockInfo last) {
        // TODO : Need to check if our memory is enough by checking the gap of the height between beforeFirst and last
        if (beforeFirstHeader == last.getBlockHeader()) {
            // The base case. Nothing to do.
        } else {
            // Note that we are constructing the blockInfos so that the order of the blocks in the blockInfos is from the oldest to the newest.
            blockInfos.add(0, last);
            if (last.getBlockHeader().getHashPrevBlock().isAllZero()) {
                logger.error("collectBlockInfos : The last block is the genesis block. beforeFirst:${beforeFirstHeader}, last:${last}");
                assert (false);
            }
            BlockInfo beforeLastOption = storage.getBlockInfo(db, last.getBlockHeader().getHashPrevBlock());
            collectBlockInfos(db, blockInfos, beforeFirstHeader, beforeLastOption);
        }
    }


    /**
     * Get the descriptor of the common ancestor of the two given blocks.
     * Because all blocks are on top of the genesis block, this method should at least return the BlockInfo for the genesis block.
     *
     * @param block1 The first given block.
     * @param block2 The second given block.
     */

    private BlockHeader findCommonBlock(KeyValueDatabase db, BlockInfo block1, BlockInfo block2) {
        if (block1.getHeight() < block2.getHeight()) {
            // back track for block2
            assert (!block2.getBlockHeader().getHashPrevBlock().isAllZero());
            BlockInfo prevBlock2 = storage.getBlockInfo(db, block2.getBlockHeader().getHashPrevBlock());
            return findCommonBlock(db, block1, prevBlock2);
        } else if (block1.getHeight() > block2.getHeight()) {
            // back track for block1
            assert (!block1.getBlockHeader().getHashPrevBlock().isAllZero());
            BlockInfo prevBlock1 = storage.getBlockInfo(db, block1.getBlockHeader().getHashPrevBlock());
            return findCommonBlock(db, prevBlock1, block2);
        } else { // block1.height == block2.height
            if (block1.getBlockHeader() == block2.getBlockHeader()) { // the base case : The common block was found
                return block1.getBlockHeader();
            } else {
                assert (!block1.getBlockHeader().getHashPrevBlock().isAllZero());
                assert (!block2.getBlockHeader().getHashPrevBlock().isAllZero());
                // the block height is same, but the block header does not match.
                // back track for block1 and block2
                BlockInfo prevBlock1 = storage.getBlockInfo(db, block1.getBlockHeader().getHashPrevBlock());
                BlockInfo prevBlock2 = storage.getBlockInfo(db, block2.getBlockHeader().getHashPrevBlock());
                return findCommonBlock(db, prevBlock1, prevBlock2);
            }
        }
    }

    /**
     * Detach a block from the best blockchain.
     *
     * @param blockInfo The BlockInfo of the block to detach.
     * @param block     The block to detach.
     */
    private void detachBlock(KeyValueDatabase db, BlockInfo blockInfo, Block block) {
        // Detach each transaction in reverse order.
        // TODO : Optimize : Iterate a list in reverse order without reversing it.

        List<Transaction> transactions = block.getTransactions();
        Collections.reverse(transactions);

        transactions.stream().forEach(transaction -> {
            txMagnet.detachTransaction(db, transaction);
        });

        storage.delBlockHashByHeight(db, blockInfo.getHeight());

        Hash prevBlockHash = blockInfo.getBlockHeader().getHashPrevBlock();
        assert (!prevBlockHash.isAllZero()); // The genesis block can't be detached
        // Unlink the next block hash.
        storage.updateNextBlockHash(db, prevBlockHash, null);
    }

    private void detachBlocksAfter(KeyValueDatabase db, BlockHeader beforeFirstHeader, BlockInfo last, Block lastBlock, List<Block> detachedBlocks) {
        if (beforeFirstHeader == last.getBlockHeader()) {
            // The base case.
        } else {
            // Construct the detachedBlocks so that it has detached blocks. Order : from oldest to newest
            detachedBlocks.add(0, lastBlock);
            detachBlock(db, last, lastBlock);

            // Get the block before the last one, continue iteration.
            Pair<BlockInfo, Block> pair = storage.getBlock(db, last.getBlockHeader().getHashPrevBlock());
            BlockInfo beforeLastInfo = pair.getKey();
            Block beforeLastBlock = pair.getValue();
            detachBlocksAfter(db, beforeFirstHeader, beforeLastInfo, beforeLastBlock, detachedBlocks);
        }
    }
}
