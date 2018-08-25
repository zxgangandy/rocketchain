package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

public class SHA256 extends HashValue {

    public SHA256(Bytes value) {
        super(value);
    }

    public Bytes getValue() {
        return value;
    }
}
