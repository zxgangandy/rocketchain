package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

public class LockingScript extends Script {

    public LockingScript(Bytes data) {
        super(data);
    }

    public Bytes getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LockingScript{" +
                HexUtil.byteArrayToHexString(data.getArray()) +
                '}';
    }
}
