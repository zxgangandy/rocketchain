package com.rocketchain.proto;

import java.math.BigInteger;

/** Version ; When a node creates an outgoing connection, it will immediately advertise its version.
 *  The remote node will respond with its version. No further communication is possible until both peers have exchanged their version.
 */
public class Version implements ProtocolMessage {

    private  int version;
    private BigInteger services;
    private Long timestamp;
    private NetworkAddress destAddress;
    private NetworkAddress sourceAddress;
    private BigInteger nonce;
    private String userAgent;
    private Long startHeight;
    private Boolean relay;

    public Version(int version, BigInteger services, Long timestamp, NetworkAddress destAddress, NetworkAddress sourceAddress,
                   BigInteger nonce, String userAgent, Long startHeight, Boolean relay) {
        this.version = version;
        this.services = services;
        this.timestamp = timestamp;
        this.destAddress = destAddress;
        this.sourceAddress = sourceAddress;
        this.nonce = nonce;
        this.userAgent = userAgent;
        this.startHeight = startHeight;
        this.relay = relay;
    }

    public int getVersion() {
        return version;
    }

    public BigInteger getServices() {
        return services;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public NetworkAddress getDestAddress() {
        return destAddress;
    }

    public NetworkAddress getSourceAddress() {
        return sourceAddress;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Long getStartHeight() {
        return startHeight;
    }

    public Boolean getRelay() {
        return relay;
    }

    @Override
    public String toString() {
        return "Version{" +
                "version=" + version +
                ", services=" + services +
                ", timestamp=" + timestamp +
                ", destAddress=" + destAddress +
                ", sourceAddress=" + sourceAddress +
                ", nonce=" + nonce +
                ", userAgent='" + userAgent + '\'' +
                ", startHeight=" + startHeight +
                ", relay=" + relay +
                '}';
    }
}
