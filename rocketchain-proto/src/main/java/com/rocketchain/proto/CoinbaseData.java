package com.rocketchain.proto;

import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

public class CoinbaseData {
    private Bytes data;

    public CoinbaseData(Bytes data) {
        this.data = data;
    }

    public Bytes getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CoinbaseData{" +
                HexUtil.byteArrayToHexString(data.getArray()) +
                '}';
    }
}
