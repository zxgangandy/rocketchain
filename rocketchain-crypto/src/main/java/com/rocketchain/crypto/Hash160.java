package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

public class Hash160 extends HashValue {
    public Hash160(Bytes value) {
        super(value);
    }

    public Bytes getValue() {
        return value;
    }
}
