package com.rocketchain.proto;

import java.util.List;

public class Inv implements ProtocolMessage {

    private List<InvVector> inventories;

    public Inv(List<InvVector> inventories) {
        this.inventories = inventories;
    }

    public List<InvVector> getInventories() {
        return inventories;
    }
}
