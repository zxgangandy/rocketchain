package com.rocketchain.proto;

public class NetworkAddressWithTimestamp {
    private long timestamp;
    private NetworkAddress address;

    public NetworkAddressWithTimestamp(long timestamp, NetworkAddress address) {
        this.timestamp = timestamp;
        this.address = address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public NetworkAddress getAddress() {
        return address;
    }
}
