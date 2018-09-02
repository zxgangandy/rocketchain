package com.rocketchain.net.p2p.handler;

import com.rocketchain.proto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The message handler for Pong message.
 */
public class PongMessageHandler {
    private Logger logger = LoggerFactory.getLogger(PongMessageHandler.class);

    /**
     * Handle Pong message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param pong    The Pong message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle(MessageHandlerContext context, Pong pong) {
        // TODO : Implement
    }
}
