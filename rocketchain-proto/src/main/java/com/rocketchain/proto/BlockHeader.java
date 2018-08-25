package com.rocketchain.proto;



public class BlockHeader {
    private int version;
    private Hash hashPrevBlock;
    private Hash hashMerkleRoot;
    private Long timestamp;
    private Long target;
    private Long nonce;

    public BlockHeader(int version, Hash hashPrevBlock, Hash hashMerkleRoot, Long timestamp, Long target, Long nonce) {
        this.version = version;
        this.hashPrevBlock = hashPrevBlock;
        this.hashMerkleRoot = hashMerkleRoot;
        this.timestamp = timestamp;
        this.target = target;
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "BlockHeader{" +
                "version=" + version +
                ", hashPrevBlock=" + hashPrevBlock +
                ", hashMerkleRoot=" + hashMerkleRoot +
                ", timestamp=" + timestamp +
                ", target=" + target +
                ", nonce=" + nonce +
                '}';
    }

    public int getVersion() {
        return version;
    }

    public Hash getHashPrevBlock() {
        return hashPrevBlock;
    }

    public Hash getHashMerkleRoot() {
        return hashMerkleRoot;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getTarget() {
        return target;
    }

    public Long getNonce() {
        return nonce;
    }
}
