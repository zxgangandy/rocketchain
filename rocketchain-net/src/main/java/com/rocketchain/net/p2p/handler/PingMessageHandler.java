package com.rocketchain.net.p2p.handler;


import com.rocketchain.proto.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The message handler for Ping message.
 */
public class PingMessageHandler {
    private Logger logger = LoggerFactory.getLogger(PingMessageHandler.class);

    /** Handle Ping message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param ping The ping message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle( MessageHandlerContext context , Ping ping  )  {
        // TODO : Implement
    }
}
