package com.rocketchain.client;

public class Parameters {
    // The address of the peer we want to connect. If this is set, rocketchain.p2p.peers is ignored.
    private String peerAddress;
    // The port of the peer we want to connect. If this is set, rocketchain.p2p.peers is ignored.
    private Integer peerPort;
    private String cassandraAddress;
    private Integer cassandraPort;
    private Integer p2pInboundPort;
    private Integer apiInboundPort;
    private String miningAccount;
    private String network;
    private Integer maxBlockSize;
    private Boolean disableMiner;

    public Parameters(String peerAddress, Integer peerPort, String cassandraAddress, Integer cassandraPort, Integer p2pInboundPort,
                      Integer apiInboundPort, String miningAccount, String network, Integer maxBlockSize, Boolean disableMiner) {
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.cassandraAddress = cassandraAddress;
        this.cassandraPort = cassandraPort;
        this.p2pInboundPort = p2pInboundPort;
        this.apiInboundPort = apiInboundPort;
        this.miningAccount = miningAccount;
        this.network = network;
        this.maxBlockSize = maxBlockSize;
        this.disableMiner = disableMiner;
    }

    public String getNetwork() {
        return network;
    }

    public Integer getP2pInboundPort() {
        return p2pInboundPort;
    }

    public String getPeerAddress() {
        return peerAddress;
    }

    public Integer getPeerPort() {
        return peerPort;
    }

    public String getCassandraAddress() {
        return cassandraAddress;
    }

    public Integer getCassandraPort() {
        return cassandraPort;
    }

    public Integer getApiInboundPort() {
        return apiInboundPort;
    }

    public String getMiningAccount() {
        return miningAccount;
    }

    public Integer getMaxBlockSize() {
        return maxBlockSize;
    }

    public Boolean getDisableMiner() {
        return disableMiner;
    }
}
