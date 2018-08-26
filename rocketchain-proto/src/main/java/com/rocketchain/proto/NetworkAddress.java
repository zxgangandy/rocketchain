package com.rocketchain.proto;


import java.math.BigInteger;

public class NetworkAddress {
    private BigInteger services;
    private IPv6Address ipv6;
    private int port;

    public NetworkAddress(BigInteger services, IPv6Address ipv6, int port)  {
        this.services = services;
        this.ipv6 = ipv6;
        this.port = port;
    }

    public BigInteger getServices() {
        return services;
    }

    public IPv6Address getIpv6() {
        return ipv6;
    }

    public int getPort() {
        return port;
    }
}
