package com.rocketchain.utils.net;

public class PeerAddress {
    private String address;
    private int port;
    public PeerAddress(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
