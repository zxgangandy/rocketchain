package com.rocketchain.net.p2p;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.net.message.GetBlocksFactory;
import com.rocketchain.proto.GetBlocks;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Node {
    private Logger logger = LoggerFactory.getLogger(Node.class);

    private static Node theNode;


    // BUGBUG : What if the best peer was a malicious node?
    // The best peer for initial block download
    private Peer bestPeerForIBD;

    private Hash lastBlockHashForIBD = Hash.ALL_ZERO;

    private PeerCommunicator peerCommunicator;
    private Blockchain chain;

    public Node(PeerCommunicator peerCommunicator, Blockchain chain) {
        this.peerCommunicator = peerCommunicator;
        this.chain = chain;
    }

    public static Node create(PeerCommunicator peerCommunicator, Blockchain chain) {
        if (theNode == null) {
            theNode = new Node(peerCommunicator, chain);
        }
        return theNode;
    }

    /**
     * Return the best height if we received version message more than two thirds of total nodes.
     *
     * @return Some(best Peer) that has the highest Version.startHeight if we received version message more than two thirds of total nodes. None otherwise.
     */
    public Peer getBestPeer() {
        int maxPeerCount = Config.get().peerAddresses().size();
        List<PeerInfo> peerInfos = peerCommunicator.getPeerInfos();

        // We have two way communication channels for each peer.
        // PeerInfos list each communication channel.
        int connectedPeers = peerInfos.size() / 2;

        // Get the total active peers including me.
        int totalActivePeers = connectedPeers + 1;

        // Do we have enough number of peers? (At least more than two thirds)
        if (totalActivePeers > maxPeerCount * 2 / 3) {
            // Did we receive starting height for all peers?
            List<PeerInfo> peers = peerInfos.stream()
                    .filter(item -> item.getStartingheight() != null)
                    .collect(Collectors.toList());


            if (peers.size() == peerInfos.size()) {
                Peer bestPeer = peerCommunicator.getBestPeer();
                return bestPeer;
            } else {
                return null;
            }
        } else { // Not enough connected peers.
            return null;
        }
    }

    public void updateStatus() {
        synchronized (this) {
            Peer bestPeerOption = getBestPeer();
            if (bestPeerOption != null) {
                Peer bestPeer = bestPeerOption;
                if ((bestPeer.getVersionOption().getStartHeight()) > chain.getBestBlockHeight()) {
                    bestPeerForIBD = bestPeer;

                    /* Start IBD(Initial block download). Request getblocks message to get inv messages for our missing blocks */
                    GetBlocks getBlocksMessage = GetBlocksFactory.create(lastBlockHashForIBD);
                    bestPeerForIBD.send(getBlocksMessage);

                    logger.info("Initial block download started. Requested getblocks message.");
                }
            }
        }
    }

    public long bestPeerStartHeight() {
        synchronized (this) {
            assert (bestPeerForIBD != null);
            return bestPeerForIBD.getVersionOption().getStartHeight();
        }
    }

    public void sendToBestPeer(ProtocolMessage message) {
        synchronized (this) {
            assert (bestPeerForIBD != null);
            bestPeerForIBD.send(message);
        }
    }

    public boolean isInitialBlockDownload() {
        synchronized (this) {
            return bestPeerForIBD != null;
        }
    }

    public void stopInitialBlockDownload() {
        synchronized (this) {
            bestPeerForIBD = null;
        }
    }


    public static Node get() {
        assert (theNode != null);
        return theNode;
    }
}
