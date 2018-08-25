package com.rocketchain.proto;

import java.util.List;

/** GetBlocks; Return an inv packet containing the list of blocks
 * starting right after the last known hash in the block locator object,
 * up to hash_stop or 500 blocks, whichever comes first.
 * The locator hashes are processed by a node in the order as they appear in the message.
 * If a block hash is found in the node's main chain, the list of its children is returned back via the inv message and
 * the remaining locators are ignored, no matter if the requested limit was reached, or not.
 *
 * To receive the next blocks hashes, one needs to issue getblocks again with a new block locator object.
 * Keep in mind that some clients may provide blocks which are invalid
 * if the block locator object contains a hash on the invalid branch.
 */
public class GetBlocks implements ProtocolMessage{

    private long version;
    private List<Hash> blockLocatorHashes;
    private Hash  hashStop ;

    public GetBlocks(long version, List<Hash> blockLocatorHashes, Hash hashStop) {
        this.version = version;
        this.blockLocatorHashes = blockLocatorHashes;
        this.hashStop = hashStop;
    }

    public long getVersion() {
        return version;
    }

    public List<Hash> getBlockLocatorHashes() {
        return blockLocatorHashes;
    }

    public Hash getHashStop() {
        return hashStop;
    }
}
