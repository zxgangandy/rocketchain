package com.rocketchain.crypto;

import com.rocketchain.utils.lang.Bytes;

public class SHA1 extends HashValue {

    public SHA1(Bytes value) {
        super(value);
    }

    public Bytes getValue() {
        return value;
    }
}

