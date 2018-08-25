package com.rocketchain.proto;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.Bytes;


import java.net.UnknownHostException;

public class IPv6Address {

    private com.rocketchain.utils.lang.Bytes address;
    public IPv6Address(com.rocketchain.utils.lang.Bytes address) {
        this.address = address;
    }

    public InetAddresses inetAddress() {
        Bytes.reverse(address.getArray());
        try {
            InetAddresses.fromLittleEndianByteArray(address.getArray());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    public com.rocketchain.utils.lang.Bytes getAddress() {
        return address;
    }
}
