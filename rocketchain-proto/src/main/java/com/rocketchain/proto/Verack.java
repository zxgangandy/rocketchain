package com.rocketchain.proto;

public class Verack implements ProtocolMessage{
    private int dummy;

    public Verack() {
        dummy = 0;
    }

    public Verack(int dummy) {
        this.dummy = dummy;
    }
}
