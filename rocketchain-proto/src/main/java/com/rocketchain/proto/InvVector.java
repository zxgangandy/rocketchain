package com.rocketchain.proto;

public class InvVector implements ProtocolMessage {
    private InvType invType;
    private Hash  hash ;

    public InvVector(InvType invType, Hash hash) {
        this.invType = invType;
        this.hash = hash;
    }

    public InvType getInvType() {
        return invType;
    }

    public Hash getHash() {
        return hash;
    }
}
