package com.rocketchain.proto;

public class BlockInfo {

    private Long height;
    // The total (estimated) number of hash calculations from the genesis block.
    private Long chainWork;
    private Hash nextBlockHash;
    private int transactionCount;
    private int status;
    private BlockHeader blockHeader  ;
    private FileRecordLocator blockLocatorOption ;

    public BlockInfo(Long height, Long chainWork, int transactionCount, Hash nextBlockHash,  int status,
                     BlockHeader blockHeader ,FileRecordLocator blockLocatorOption) {
        this.height = height;
        this.chainWork = chainWork;
        this.nextBlockHash = nextBlockHash;
        this.transactionCount = transactionCount;
        this.status = status;
        this.blockHeader = blockHeader;
        this.blockLocatorOption = blockLocatorOption;
    }

    public Long getHeight() {
        return height;
    }

    public Long getChainWork() {
        return chainWork;
    }

    public Hash getNextBlockHash() {
        return nextBlockHash;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getStatus() {
        return status;
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public FileRecordLocator getBlockLocatorOption() {
        return blockLocatorOption;
    }
}
