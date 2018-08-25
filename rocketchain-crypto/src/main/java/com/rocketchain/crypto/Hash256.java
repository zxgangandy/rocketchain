package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

public class Hash256 extends HashValue {
    public Hash256(Bytes value) {
        super(value);
    }

    public Bytes getValue() {
        return value;
    }

}
