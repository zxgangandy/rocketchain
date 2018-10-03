package com.rocketchain.utils;

import com.rocketchain.utils.net.PeerAddress;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Config {
    public static int MAX_BLOCK_SIZE = 1024 * 1024;
    public static int MAX_PUBLIC_KEYS_FOR_MULTSIG = 20;
    private com.typesafe.config.Config config;

    private static Config theConfig = new Config(ConfigFactory.parseFile(
            new File(GlobalEnvironemnt.RocketChainHome + "config/rocketchain.conf")));

    public Config(com.typesafe.config.Config config) {
        this.config = config;
    }

    public static Config get() {
        return theConfig;
    }

    public boolean hasPath(String path) {
        return config.hasPath(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public List<? extends com.typesafe.config.Config> getConfiglistOf(String path) {
        return config.getConfigList(path);
    }

    public List<PeerAddress> peerAddresses()  {
        return getConfiglistOf("rocketchain.p2p.peers").stream().map(peer -> {
             return new PeerAddress(peer.getString("address"), peer.getInt("port"));
        }).collect(Collectors.toList());
    }
}
