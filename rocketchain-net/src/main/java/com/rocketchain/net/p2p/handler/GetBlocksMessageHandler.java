package com.rocketchain.net.p2p.handler;

import com.rocketchain.chain.BlockLocator;
import com.rocketchain.chain.BlockLocatorHashes;
import com.rocketchain.chain.Blockchain;
import com.rocketchain.net.message.InvFactory;
import com.rocketchain.proto.GetBlocks;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Inv;
import com.rocketchain.storage.index.KeyValueDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetBlocksMessageHandler {
    private Logger logger = LoggerFactory.getLogger(GetBlocksMessageHandler.class);

    private final static  int MAX_HASH_PER_REQUEST = 500;

    /** Handle GetBlocks message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param getBlocks The GetBlocks message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle(MessageHandlerContext context, GetBlocks getBlocks) {
        // TODO : Investigate : Need to understand : GetDistanceBack returns the depth(in terms of the sender's blockchain)
        // of the block that is in our main chain. It returns 0 if the tip of sender's branch is in our main chain. We will
        // send up to 500 more blocks from the tip height of the sender's chain.

        KeyValueDatabase db  = Blockchain.get().getDb();

        // Step 1 : Get the list of block hashes to send.
        BlockLocator locator = new BlockLocator(Blockchain.get());

        // Step 2 : Skip the common block, start building the list of block hashes from the next block of the common block.
        //          Stop constructing the block hashes if we hit the count limit, 500. GetBlocks sends up to 500 block hashes.
        List<Hash> blockHashes = locator.getHashes(new BlockLocatorHashes(getBlocks.getBlockLocatorHashes()),
                getBlocks.getHashStop(), MAX_HASH_PER_REQUEST);

        // TODO : BUGBUG : Bitcoin Core compatibility - Need to drop the last hash if it matches getBlocks.hashStop.
        List<Hash> filteredBlockHashes = blockHashes;
        // Step 3 : Remove the hashStop if it is the last element of the list. GetBlocks does not send the hashStop block as an Inv.
/*
      if (blockHashes.lastOption.isDefined && blockHashes.lastOption.get == getBlocks.hashStop) {
        blockHashes.dropRight(1)
      } else {
        blockHashes
      }
*/

        // Step 4 : Pack the block hashes into an Inv message, and reply it to the requester.
        if (filteredBlockHashes.isEmpty()) {
            // Do nothing. Nothing to send.
            logger.trace("Nothing to send in response to getblocks message.");
        } else {
            Inv invMessage = InvFactory.createBlockInventories(filteredBlockHashes);
            context.getPeer().send(invMessage);
            logger.trace("Sending inventories in response to getblocks message. ${MessageSummarizer.summarize(invMessage)}");
        }
    }
}
