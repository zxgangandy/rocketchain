package com.rocketchain.chain.mining;

import com.rocketchain.chain.MerkleRootCalculator;
import com.rocketchain.chain.transaction.NetEnv;
import com.rocketchain.chain.transaction.NetEnvFactory;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;

import java.util.List;

public class BlockTemplate {
    private long difficultyBits ;
    private List<Transaction> sortedTransactions ;

    /** The template of a block for creating a block.
     * It has list of transactions to put into a block.
     *
     * The transactions are sorted, and they are chosen from the mempool based on (1) priority and (2) fee.
     * We need to sort the transactions, and calculate a block header for finding out the nonce that produces a block header
     * which is less than or equal to the minimum block header hash calculated from the difficulty bits in the block header.
     *
     * @param difficultyBits the 4 byte integer representing the hash difficulty. This value is stored as the block header's target.
     * @param sortedTransactions the sorted transactions to add to the block.
     *
     */
    public BlockTemplate(long difficultyBits, List<Transaction> sortedTransactions) {
        this.difficultyBits = difficultyBits;
        this.sortedTransactions = sortedTransactions;
    }

    public long getDifficultyBits() {
        return difficultyBits;
    }

    public List<Transaction> getSortedTransactions() {
        return sortedTransactions;
    }

    // TODO : Use difficultyBits

    /** Get the block header from this template.
     *
     * @param prevBlockHash the hash of the previous block header.
     * @return The block header created from this template.
     */
    public BlockHeader getBlockHeader(Hash prevBlockHash )  {
        // Step 1 : Calculate the merkle root hash.
        Hash merkleRootHash = MerkleRootCalculator.calculate(sortedTransactions);

        NetEnv env = NetEnvFactory.get();

        // Step 2 : Create the block header
        return new BlockHeader(env.DefaultBlockVersion, prevBlockHash, merkleRootHash, System.currentTimeMillis()/1000,
                difficultyBits, 0L);
    }

    /** Create a block based on the block header and nonce.
     *
     * @param blockHeader The block header we got by calling getBlockHeader method.
     * @param nonce The nonce we found by calling findNonce method.
     * @return The created block that has all transactions in this template with a valid block header.
     */
    public Block createBlock(BlockHeader blockHeader, long nonce)  {
        blockHeader.setNonce(nonce);
        return new  Block( blockHeader, sortedTransactions );
    }
}
