package com.rocketchain.storage;

import com.rocketchain.codec.BlockHeaderCodec;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.codec.TransactionCodec;
import com.rocketchain.codec.TransactionCountCodec;
import com.rocketchain.proto.*;
import com.rocketchain.storage.record.BlockRecordStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Append a block to the given disk block storage,
 * producing file record locators for the block as well as each transaction in the block.
 * <p>
 * Why? When we put a block into disk block storage, we have to create an index by block hash.
 * We also have to create an index by transaction hash that points to each transaction in the written block.
 * <p>
 * This is necessary to read a specific transaction by hash, to get unspent output using an out point.
 * (An out point points to an output of a transaction using transaction hash and output index. )
 * <p>
 * Note : When a new record file is created between appending a block header and appending transactions,
 * we have to append block on the file again.
 * <p>
 * Otherwise, our logic to calculate the size of the block is too complicated,
 * as we assume that the block header and transactions are written in the same record file.
 *
 */
public class BlockWriter {
    private BlockRecordStorage storage;

    /**
     * Write a block on the disk block storage.
     *
     * @param storage The block record storage where we write our block and transactions in it.
     */
    public BlockWriter(BlockRecordStorage storage) {
        this.storage = storage;
    }

    /**
     * Append a block to the given disk block storage,
     * producing file record locators for the block as well as each transaction in the block.
     * <p>
     * Why? When we put a block into disk block storage, we have to create an index by block hash.
     * We also have to create an index by transaction hash that points to each transaction in the written block.
     * <p>
     * This is necessary to read a specific transaction by hash, to get unspent output using an out point.
     * (An out point points to an output of a transaction using transaction hash and output index. )
     * <p>
     * Note : When a new record file is created between appending a block header and appending transactions,
     * we have to append block on the file again.
     * <p>
     * Otherwise, our logic to calculate the size of the block is too complicated,
     * as we assume that the block header and transactions are written in the same record file.
     *
     * @param block The block to write.
     * @return AppendBlockResult, which is the header locator and transaction locators.
     */
    public AppendBlockResult appendBlock(Block block) {
        try {
            return appendBlockInternal(block);
        } catch (RecordFileChangedWhileWritingBlock e) {
            // A new block was created while writing a block
            // Call appendBlockInternal again, to append the block on the new file.
            return appendBlockInternal(block);
        }
    }

    /**
     * An internal version of the appendBlock. Throws an exception if a new record file was created between appending a block header and appending transactions.
     * <p>
     * Need to keep appendBlockInternal consistent with BlockWriter.getTxLocators.
     * - Whenever the block format changes, we need to apply the change to both methods.
     *
     * @param block The block to append
     * @return The AppendBlockResult.
     * @throws BlockWriter.RecordFileChangedWhileWritingBlock A new record file was created between appending a block header and appending transactions.
     *                                                        The caller should call this function again, to append the block on the new file.
     */
    private AppendBlockResult appendBlockInternal(Block block) {
        /**
         * To get a record locator of each transaction we write while we write a block,
         * We need to write (1) block header (2) transaction count (3) each transaction.
         *
         * This is why we need a separate data class for the transaction count.
         * If we write a block as a whole, we can't get record locators for each transaction in a block.
         */

        // Step 1 : Write block header
        FileRecordLocator blockHeaderLocator = storage.appendRecord(new BlockHeaderCodec(), block.getHeader());

        // Step 2 : Write transaction count
        storage.appendRecord(new TransactionCountCodec(), new TransactionCount(block.getTransactions().size()));

        // Step 3 : Write each transaction
        List<TransactionLocator> txLocators = block.getTransactions().stream()
                .map(transaction -> {
                    FileRecordLocator txLocator = storage.appendRecord(new TransactionCodec(), transaction);
                    // Step 31 : Check if a file was created during step 2 or step 3.
                    if (blockHeaderLocator.getFileIndex() != txLocator.getFileIndex()) {
                        // BUGBUG : We have written a transaction on the file, and the space is wasted.
                        throw new RecordFileChangedWhileWritingBlock();
                    }
                    return new TransactionLocator(HashUtil.hashTransaction(transaction), txLocator);
                })
                .collect(Collectors.toList());

        // Step 4 : Calculate block locator
        // The AppendBlockResult.headerLocator has its size 80(the size of block header)
        // We need to use the last transaction's (offset + size), which is the block size to get the block locator.
        RecordLocator lastTxLocator = txLocators.get(txLocators.size() - 1).getTxLocator().getRecordLocator();
        long blockSizeExceptTheLastTransaction = lastTxLocator.getOffset() - blockHeaderLocator.getRecordLocator().getOffset();

        // CRITICAL BUGBUG : If a record storage file is added, the last transaction and the block header is written in different file.
        long blockSize = blockSizeExceptTheLastTransaction + lastTxLocator.getSize();

        //println(s"blockHeaderLocator=${blockHeaderLocator}, txLocators.last.txLocator=${txLocators.last.txLocator}, blockSizeExceptTheLastTransaction=$blockSizeExceptTheLastTransaction, blockSize=$blockSize")

        RecordLocator recordLocator = blockHeaderLocator.getRecordLocator();
        recordLocator.setSize((int) blockSize);
        blockHeaderLocator.setRecordLocator(recordLocator);

        FileRecordLocator blockLocator = blockHeaderLocator;

        return new AppendBlockResult(blockLocator, blockHeaderLocator, txLocators);
    }


    /**
     * Note :
     * - Need to keep appendBlockInternal consistent with BlockWriter.getTxLocators.
     * - Whenever the block format changes, we need to apply the change to both methods.
     *
     * @param blockLocator The locator of the block, where points to the on-disk location.
     * @param block        The block data.
     * @return The list of transaction locators for each transaction in the block.
     */
    public static List<TransactionLocator> getTxLocators(FileRecordLocator blockLocator, Block block) {
        // Step 1 : Calculate the size of block header
        int blockHeaderSize = new BlockHeaderCodec().encode(block.getHeader()).length;

        // Step 2 : Calculate the size of transaction count
        int transactionCountSize = new TransactionCountCodec().encode(
                new TransactionCount(block.getTransactions().size())).length;

        // Step 3 : Calculate the transaction offset for the first transaction
        long transactionOffset = blockLocator.getRecordLocator().getOffset() + blockHeaderSize + transactionCountSize;

        // Step 4 : Calculate transaction locator of each transaction.

        List<Transaction> transactions = block.getTransactions();
        List<TransactionLocator> txLocators = new ArrayList<>();
        for (Transaction transaction : transactions) {
            int transactionSize = new TransactionCodec().encode(transaction).length;
            blockLocator.setRecordLocator(new RecordLocator(transactionOffset, transactionSize));
            FileRecordLocator txLocator = blockLocator;
            transactionOffset += transactionSize;
            TransactionLocator locator = new TransactionLocator(HashUtil.hashTransaction(transaction), txLocator);
            txLocators.add(locator);
        }

        return txLocators;
    }

    private static class RecordFileChangedWhileWritingBlock extends RuntimeException {
        public RecordFileChangedWhileWritingBlock() {
            super();
        }
    }

}
