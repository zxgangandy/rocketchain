package com.rocketchain.client;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandArgument {

    private static Options options;

    static {
        options = new Options();

        options.addOption(Option.builder("p")
                .longOpt("p2pPort")
                .hasArg()
                .desc("The P2P inbound port to use to accept connection from other peers.")
                .build());


        options.addOption(Option.builder("c")
                .longOpt("apiPort")
                .hasArg()
                .desc("The API inbound port to use to accept connection from RPC clients.")
                .build());

        options.addOption(Option.builder("a")
                .longOpt("peerAddress")
                .hasArg()
                .desc("The address of the peer we want to connect.")
                .build());


        options.addOption(Option.builder("x")
                .longOpt("peerPort")
                .hasArg()
                .desc("The port of the peer we want to connect.")
                .build());


        options.addOption(Option.builder("m")
                .longOpt("miningAccount")
                .hasArg()
                .desc("The account to get the coins mined. The receiving address of the account will get the coins mined.")
                .build());


        options.addOption(Option.builder("n")
                .longOpt("network")
                .hasArg()
                .desc("The network to use. currently 'testnet' is supported. Will support 'mainnet' as well as 'regtest' soon.")
                .build());


        options.addOption(Option.builder()
                .longOpt("disableMiner")
                .desc("Disable coin miner.")
                .build());
    }

    public static Options getOptions() {
        return options;
    }
}
