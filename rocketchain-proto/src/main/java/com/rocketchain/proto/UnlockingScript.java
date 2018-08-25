package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;

public class UnlockingScript extends Script {
    public UnlockingScript(Bytes data) {
        super(data);
    }

    public Bytes getData() {
        return data;
    }
}
