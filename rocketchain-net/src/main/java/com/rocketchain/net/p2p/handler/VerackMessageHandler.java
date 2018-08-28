package com.rocketchain.net.p2p.handler;

import com.rocketchain.proto.Verack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerackMessageHandler {

    private Logger logger = LoggerFactory.getLogger(VerackMessageHandler.class);

    /** Handle Verack message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param message The message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle( MessageHandlerContext context , Verack message  )  {
        // TODO : Implement

        logger.info("received Verack message: {}", message);
    }
}
