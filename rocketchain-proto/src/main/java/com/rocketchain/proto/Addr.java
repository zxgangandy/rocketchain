package com.rocketchain.proto;

import java.util.List;

public class Addr implements ProtocolMessage {
    private List<NetworkAddressWithTimestamp> addresses;

    public Addr(List<NetworkAddressWithTimestamp> addresses) {
        this.addresses = addresses;
    }

    public List<NetworkAddressWithTimestamp> getAddresses() {
        return addresses;
    }
}
