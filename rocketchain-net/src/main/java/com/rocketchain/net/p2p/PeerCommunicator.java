package com.rocketchain.net.p2p;


import com.google.common.collect.Lists;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.proto.Transaction;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class PeerCommunicator {
    private PeerSet peerSet;

    public PeerCommunicator(PeerSet peerSet) {
        this.peerSet = peerSet;
    }

    public void sendToAll( ProtocolMessage message) {
        peerSet.sendToAll(message);
    }

    /** Propagate a newly mined block to the peers. Called by a miner, whenever a block was mined.
     *
     * @param block The newly mined block to propagate.
     */
    public void propagateBlock(Block block ) {
        // Propagating a block is an urgent job to do. Without broadcasting the inventories, send the block itself to the network.
        sendToAll(block);
    }

    /** Propagate a newly received transaction to the peers.
     *
     * @param transaction The transaction to propagate.
     */
    public void propagateTransaction(Transaction transaction )  {
        sendToAll(transaction);
    }

    /** Get the list of information on each peer.
     *
     * Used by : getpeerinfo RPC.
     *
     * @return The list of peer information.
     */
    public List<PeerInfo> getPeerInfos()  {
        int peerIndex = 0;

        List<Pair<InetSocketAddress, Peer>> list = peerSet.peers();
        List<PeerInfo> peerInfoList = Lists.newArrayList();

        for (Pair<InetSocketAddress, Peer> pair : list) {
            InetSocketAddress address = pair.getKey();
            Peer peer = pair.getValue();
            peerIndex ++;
            PeerInfo info = PeerInfo.create(peerIndex, address, peer);
            peerInfoList.add(info);
        }

        return peerInfoList;
    }

    /**
     * Get the peer which has highest best block height.
     *
     * @return Some(best Peer) if there is any connected peer; None otherwise.
     */
    public  Peer getBestPeer()  {
        List<Peer> peers = peerSet.peers()
                .stream()
                .map(Pair::getValue)
                .collect(Collectors.toList());

        if (peers.isEmpty()) {
            return null;
        } else {
            return peers.stream().reduce(this::betterPeer).get();
        }
    }


    private Peer betterPeer(Peer peer1 , Peer peer2 )   {
        if ( (peer1.getVersionOption().getStartHeight()) > (peer2.getVersionOption().getStartHeight()) )
            return peer1;
        else
            return peer2;
    }

}
