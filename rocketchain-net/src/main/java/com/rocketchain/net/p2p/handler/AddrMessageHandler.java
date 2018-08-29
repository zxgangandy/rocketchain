package com.rocketchain.net.p2p.handler;

import com.rocketchain.proto.Addr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddrMessageHandler {
    private Logger logger = LoggerFactory.getLogger(AddrMessageHandler.class);

    /** Handle Addr message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param addr The Addr message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle( MessageHandlerContext context , Addr addr  )  {
        // TODO : Implement
    }
}
