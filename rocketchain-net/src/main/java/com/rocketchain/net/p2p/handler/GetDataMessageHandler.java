package com.rocketchain.net.p2p.handler;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.chain.processor.BlockProcessor;
import com.rocketchain.chain.processor.TransactionProcessor;
import com.rocketchain.net.message.MessageSummarizer;
import com.rocketchain.proto.GetData;
import com.rocketchain.proto.InvType;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.storage.index.KeyValueDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The message handler for GetData message.
 */
public class GetDataMessageHandler {
    private Logger logger = LoggerFactory.getLogger(GetDataMessageHandler.class);

    /** Handle GetData message.
     *
     * @param context The context where handlers handling different messages for a peer can use to store state data.
     * @param getData The GetData message to handle.
     * @return Some(message) if we need to respond to the peer with the message.
     */
    public void handle( MessageHandlerContext context , GetData getData  )  {
        KeyValueDatabase db = Blockchain.get().getDb();
        // TODO : Step 1 : Return an error if the number of inventories is greater than 50,000.
        // Step 2 : For each inventory, send data for it.
        List<ProtocolMessage> messagesToSend = getData.getInventories().stream().map(invVector -> {
                    if (invVector.getInvType() == InvType.MSG_TX) {
                        // Get the transaction we have. Orphan transactions are not returned.
                        // TODO : send tx message only if it is in the relay memory. A 'tx' is
                        // put into the relay memory by sendfrom, sendtoaddress, sendmany RPC.
                        // For now, send a transaction if we have it.
                        // Returns Option<Transaction>

                        return new TransactionProcessor().getTransaction(db, invVector.getHash());
                    } else if (invVector.getInvType() == InvType.MSG_TX) {
                        // Get the block we have. Orphan blocks are not returned.
                        // Returns Option<Block>
                        return BlockProcessor.get().getBlock(invVector.getHash());
                    } else {
                        logger.warn("Unknown inventory type for the inventory : ${inventory}");
                        return null;
                    }
                }
        ).filter(Objects::nonNull).collect(Collectors.toList());


        // Step 3 : Send data messages ( either Transaction or Block )
        messagesToSend.stream().forEach(message->{
            logger.trace("Responding to getdata. Message : {}", MessageSummarizer.summarize(message));
            context.getPeer().send(message);
        });

        // TODO : Step 4 : Need to send NotFound message for not found block or transaction.
        // This is necessary for the SPV clients. We will implement this feature when we support SPV clients.
    }
}
