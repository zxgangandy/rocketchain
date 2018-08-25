package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;

public class Script {
    protected Bytes data;

    public Script(Bytes data) {
        this.data = data;
    }

    public int size() {
        return data.getArray().length;
    }

    public byte get(int i) {
        return data.getArray()[i];
    }
}
