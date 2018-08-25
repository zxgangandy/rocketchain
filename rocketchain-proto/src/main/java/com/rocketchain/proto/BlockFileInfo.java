package com.rocketchain.proto;

public class BlockFileInfo {
    private int blockCount;
    private long fileSize;
    private long firstBlockHeight;
    private long lastBlockHeight;
    private long firstBlockTimestamp;
    private long lastBlockTimestamp;

    public BlockFileInfo(int blockCount,
                         long fileSize,
                         long firstBlockHeight,
                         long lastBlockHeight,
                         long firstBlockTimestamp,
                         long lastBlockTimestamp) {

        this.blockCount = blockCount;
        this.fileSize = fileSize;
        this.firstBlockHeight = firstBlockHeight;
        this.lastBlockHeight = lastBlockHeight;
        this.firstBlockTimestamp = firstBlockTimestamp;
        this.lastBlockTimestamp = lastBlockTimestamp;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getFirstBlockHeight() {
        return firstBlockHeight;
    }

    public long getLastBlockHeight() {
        return lastBlockHeight;
    }

    public long getFirstBlockTimestamp() {
        return firstBlockTimestamp;
    }

    public long getLastBlockTimestamp() {
        return lastBlockTimestamp;
    }
}
