package com.rocketchain.net.p2p.handler;

import com.rocketchain.proto.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The message handler for Headers message.
 */
public class HeadersMessageHandler {
    private Logger logger = LoggerFactory.getLogger(HeadersMessageHandler.class);

    /**
     * Handle Headers message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param headers The Headers message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle(MessageHandlerContext context, Headers headers) {
        // We don't support the headers first approach yet.
        logger.warn("Headers message is not supported yet.");

    /*
        var prevHeaderHash : Hash = null
        // Step 1 : Accept block headers received.
        headers.headers foreach { header : BlockHeader =>
          if (prevHeaderHash != null) {
            if (prevHeaderHash != header.hashPrevBlock) {
              val message = s"Headers message contains non-continuous block headers. Expected previous header hash ${prevHeaderHash}, actual header : ${header}"
              logger.warn(message)
              // TODO : Increase DoS score.
              throw NetException(ErrorCode.NonContinuousBlockHeaders)
            }
          }

          BlockProcessor.acceptBlockHeader(header)
        }
        // Step 2 : Request next block headers.
        // BUGBUG : We need to construct block locators instead of simply sending the last block header hash we received.
        val getHeadersMessage = GetHeadersFactory.create( listOf( headers.headers.last.hash ) )
        context.peer.send(getHeadersMessage)
    */


/*
    // Step 1 : read block headers
    // Step 2 : Accept block headers.
    AcceptBlockHeader(header, state, &pindexLast)
        - Step 1 : Check if the block header already exists, return the block index of it if it already exists.
        - Step 2 : Check the proof of work and block timestamp.
        CheckBlockHeader(block, state)

        - Step 3 : Get the block index of the previous block.
        - Step 4 : Check proof of work, block timestamp, block checkpoint, block version based on majority of recent block versions.
        ContextualCheckBlockHeader(block, state, pindexPrev)

        - Step 5 : Add the block as a block index.
        AddToBlockIndex(block)

    // Step 3 : Request next block headers using "getheaders" message
    // Step 4 : TODO : Check Block Index.


*/
    }
}
