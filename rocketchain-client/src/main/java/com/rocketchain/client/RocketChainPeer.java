package com.rocketchain.client;

import com.google.common.collect.Lists;
import com.rocketchain.chain.Blockchain;
import com.rocketchain.chain.processor.BlockProcessor;
import com.rocketchain.chain.transaction.NetEnv;
import com.rocketchain.chain.transaction.NetEnvFactory;
import com.rocketchain.net.p2p.Node;
import com.rocketchain.net.p2p.PeerCommunicator;
import com.rocketchain.net.p2p.PeerToPeerNetworking;
import com.rocketchain.storage.BlockStorage;
import com.rocketchain.storage.DiskBlockStorage;
import com.rocketchain.storage.index.DatabaseFactory;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.Storage;
import com.rocketchain.utils.net.NetUtil;
import com.rocketchain.utils.net.PeerAddress;
import org.apache.commons.cli.*;
import com.rocketchain.utils.Config;
import com.rocketchain.utils.lang.StringUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class RocketChainPeer {

    public static void main(String argv[]) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options  = CommandArgument.getOptions();
        CommandLine line = parser.parse(options, argv);

        String argPeerAddress = line.getOptionValue("peerAddress", null);

        String argP2pPort = line.getOptionValue("p2pPort");
        Integer p2pPort = CommandArgumentConverter.toInt( "p2pPort",  argP2pPort);
        p2pPort = p2pPort != null ? p2pPort : Config.get().getInt("rocketchain.p2p.port");

        String argNetwork = line.getOptionValue("network", null);
        String network =  Config.get().getString("rocketchain.network.name");
        network = StringUtil.isEmpty(argNetwork) ? network : argNetwork;

        String argPeerPort = line.getOptionValue("peerPort", null);
        Integer peerPort = CommandArgumentConverter.toInt( "p2pPort",  argPeerPort);

        boolean disableMiner = line.hasOption("disableMiner");

        Parameters params = new Parameters(
                argPeerAddress,
                peerPort,
                null,
                0,
                p2pPort,
                0,
                null,
                network,
                Config.MAX_BLOCK_SIZE,
                disableMiner);

        initializeSystem(params);
    }

    /** Initialize sub-moudles from the lower layer to the upper layer.
     *
     * @param params The command line parameter of RocketChain.
     */
    private static void initializeSystem(Parameters params) {

        // Step 1 : Create the net environment.
        NetEnv env = NetEnvFactory.create(params.getNetwork());
        if (env == null) {
            System.out.println("Invalid p2p network : " + params.getNetwork());
            System.exit(-1);
        }

        // Step 2 : Storage Layer : Initialize block storage.
        File blockStoragePath = blockStoragePath(params.getP2pInboundPort());
        Storage.initialize();
        KeyValueDatabase db   = DatabaseFactory.create(blockStoragePath);
        BlockStorage storage = DiskBlockStorage.create(blockStoragePath, db);

        // Step 3 : Chain Layer : Initialize blockchain.
        // BUGBUG : Need to change the folder name according to the production env.
        Blockchain chain = Blockchain.create(db, storage);
        BlockProcessor.create(chain);


        // See if we have genesis block. If not, put one.
        if ( ! chain.hasBlock(db, env.getGenesisBlockHash()) ) {
            chain.putBlock(db, env.getGenesisBlockHash(), env.getGenesisBlock());
        }

        assert( chain.getBestBlockHash(db) != null );

        // Step 5 : Net Layer : Initialize peer to peer communication system, and
        // return the peer communicator that knows how to propagate blocks and transactions to peers.

        PeerCommunicator communicator = initializeNetLayer(params, chain);

        // Step 6 : CLI Layer : Create a miner that gets list of transactions from the Blockchain
        // and create blocks to submmit to the Blockchain.
    }

    private static File blockStoragePath(int argP2pPort) {
        return new File("./build/blockstorage-" + argP2pPort);
    }


    private static PeerCommunicator initializeNetLayer(Parameters params, Blockchain chain )  {
//      (addr.address == "localhost" || addr.address == "127.0.0.1") && (addr.port == params.p2pInboundPort)


        /**
         * Read list of peers from rocketchain.conf
         * It contains list of peer address and port.
         *
         * rocketchain {
         *   p2p {
         *     peers = [
         *       { address:"127.0.0.1", port:"7643" },
         *       { address:"127.0.0.1", port:"7644" },
         *       { address:"127.0.0.1", port:"7645" }
         *     ]
         *   }
         * }
         */
        List<PeerAddress>  peerAddresses;
        // If the command parameter has -peerAddress and -peerPort, connect to the given peer.
        if (params.getPeerAddress() != null && params.getPeerPort() != null) {
            peerAddresses = Lists.newArrayList(new PeerAddress(params.getPeerAddress(), params.getPeerPort()));
        } else {
            // Otherwise, connect to peers listed in the configuration file.
            peerAddresses = Config.get().peerAddresses();
        }

        peerAddresses = peerAddresses.stream()
                .filter(peer->!isMyself(peer, params.getP2pInboundPort()))
                .collect(Collectors.toList());
        PeerCommunicator communicator = PeerToPeerNetworking.createPeerCommunicator(params.getP2pInboundPort(), peerAddresses);

        Node.create(communicator, chain);

        return communicator;
    }

    private static boolean isMyself(PeerAddress addr, int port)  {
        return NetUtil.getLocalAddresses().contains(addr.getAddress()) && addr.getPort() == port;
    }

}
