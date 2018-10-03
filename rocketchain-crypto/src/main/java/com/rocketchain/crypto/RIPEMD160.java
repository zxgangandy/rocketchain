package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

public class RIPEMD160 extends HashValue {
    public RIPEMD160(Bytes value) {
        super(value);
    }

    public Bytes getValue() {
        return value;
    }
}
