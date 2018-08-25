package com.rocketchain.net.p2p;

import java.net.InetSocketAddress;

public class PeerInfo {
    private int id; // 9
    // The IP address and port number used for the connection to the remote node.
    private String addr;
    private int version; // 70001
    // The user agent this node sends in its version message.
    // This string will have been sanitized to prevent corrupting the JSON results. May be an empty string
    private String subver;// "/Satoshi:0.8.6/"
    // Set to true if this node connected to us; set to false if we connected to this node
    //  inbound : Boolean, // false
    // The height of the remote node’s block chain when it connected to us as reported in its version message
    private Long startingheight;


    public PeerInfo(int id, String addr, int version, String subver, Long startingheight) {
        this.id = id;
        this.addr = addr;
        this.version = version;
        this.subver = subver;
        this.startingheight = startingheight;
    }


    public static PeerInfo create(int peerIndex, InetSocketAddress remoteAddress, Peer peer) {
        return new PeerInfo(
                peerIndex,
                remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort(),
                peer.getVersionOption().getVersion(),
                peer.getVersionOption().getUserAgent(),
                peer.getVersionOption().getStartHeight());
    }

    public int getId() {
        return id;
    }

    public String getAddr() {
        return addr;
    }

    public int getVersion() {
        return version;
    }

    public String getSubver() {
        return subver;
    }

    public Long getStartingheight() {
        return startingheight;
    }
}
