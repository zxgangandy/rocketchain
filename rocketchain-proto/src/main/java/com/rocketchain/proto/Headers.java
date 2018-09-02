package com.rocketchain.proto;

import java.util.List;

public class Headers implements ProtocolMessage {
    private List<BlockHeader> headers;

    public Headers(List<BlockHeader> headers) {
        this.headers = headers;
    }

    public List<BlockHeader> getHeaders() {
        return headers;
    }
}
