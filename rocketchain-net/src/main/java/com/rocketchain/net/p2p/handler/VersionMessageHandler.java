package com.rocketchain.net.p2p.handler;

import com.rocketchain.net.p2p.Node;
import com.rocketchain.proto.Version;
import com.rocketchain.proto.Verack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The message handler for Version message.
 */
public class VersionMessageHandler {
    private Logger logger = LoggerFactory.getLogger(VersionMessageHandler.class);

    /**
     * Handle Version message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param version The Version message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle(MessageHandlerContext context, Version version) {
        logger.info("Version accepted : {}", version);
        context.getPeer().updateVersion(version);
        context.getPeer().send(new Verack());
        Node.get().updateStatus();
    }
}
