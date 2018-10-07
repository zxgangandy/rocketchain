package com.rocketchain.client;

import com.rocketchain.chain.BlockMining;
import com.rocketchain.chain.Blockchain;
import com.rocketchain.chain.mining.BlockTemplate;
import com.rocketchain.chain.transaction.CoinAddress;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.net.p2p.BlockPropagator;
import com.rocketchain.net.p2p.Node;
import com.rocketchain.net.p2p.Peer;
import com.rocketchain.net.p2p.PeerCommunicator;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.CoinbaseData;
import com.rocketchain.proto.Hash;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.Config;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HashEstimation;
import com.rocketchain.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Random;

public class CoinMiner {

    private Logger logger = LoggerFactory.getLogger(CoinMiner.class);

    // For every 10 seconds, create a block template for mining a block.
    // This means that transactions received within the time window may not be put into the mined block.
    private static final int MINING_TRIAL_WINDOW_MILLIS = 10000;


    private KeyValueDatabase db;
    private String minerAccount;
    private Blockchain chain;
    private PeerCommunicator peerCommunicator;
    private CoinMinerParams params;
    private CoinMinerListener listener;
    private Wallet wallet;


    private static CoinMiner theCoinMiner;

    public static CoinMiner create(KeyValueDatabase indexDb, String minerAccount, Wallet wallet, Blockchain chain,
                                   PeerCommunicator peerCommunicator, CoinMinerParams params) {
        theCoinMiner = new CoinMiner(indexDb, minerAccount, wallet, chain, peerCommunicator, params, null);
        return theCoinMiner;
    }

    public static CoinMiner get() {
        assert (theCoinMiner != null);
        return theCoinMiner;
    }

    public static CoinbaseData coinbaseData(long height) {
        String str = "height:" + height + ",RocketChain by andy";
        return new CoinbaseData(new Bytes(str.getBytes(Charset.forName("UTF-8"))));
    }


    public CoinMiner(KeyValueDatabase db, String minerAccount, Wallet wallet  , Blockchain chain, PeerCommunicator peerCommunicator,
                     CoinMinerParams params, CoinMinerListener listener) {
        this.db = db;
        this.minerAccount = minerAccount;
        this.chain = chain;
        this.peerCommunicator = peerCommunicator;
        this.params = params;
        this.listener = listener;
    }

    /**
     * Check if we can start mining.
     *
     * @return true if we can mine; false otherwise.
     */
    public boolean canMine() {
        int maxPeerCount = Config.get().peerAddresses().size();
        Node node = Node.get();
        if (maxPeerCount == 1) {
            // regression test mode with only one node.
            return true;
        } else if (node.isInitialBlockDownload()) { // During the initial block download, do not do mining.
            return false;
        } else {
            Peer bestPeerOption = node.getBestPeer();
            if (bestPeerOption != null) {
                Peer bestPeer = bestPeerOption;
                long bestBlockHeight = chain.getBestBlockHeight();

                // Did we catch up the best peer, which has the highest block height by the time we connected ?
                Long startHeight = bestPeer.getVersionOption().getStartHeight();
                startHeight = startHeight == null ? 0 : startHeight;
                return bestBlockHeight >= startHeight;
            } else {
                return false;
            }
        }
    }

    Thread thread =  new Thread( () -> {

        logger.info("Miner started. Params : ${params}");
        Random random = new Random(System.currentTimeMillis());

        int nonce = 1;

        listener.onStart();

        while (true) { // This thread loops until the shouldStop flag is set.
            nonce += 1;
            // Randomly sleep from 100 to 200 milli seconds. On average, sleep 60 seconds.
            // Because current difficulty(max hash : 00F0.. ) is to find a block at the probability 1/256,
            // We will get a block in (100ms * 256 = 25 seconds) ~ (200 ms * 256 = 52 seconds)

            long bestBlockHeight = chain.getBestBlockHeight();

            //println(s"canMine=${canMine}, isMyTurn=${isMyTurn}")

            if (canMine()) {
                //chain.synchronized {
                // Step 2 : Create the block template
                CoinAddress minerAddress = wallet.getReceivingAddress(db, minerAccount);
                Hash bestBlockHash = chain.getBestBlockHash(db);
                if (bestBlockHash != null) {
                    long blockHeight = chain.getBlockInfo(db, bestBlockHash).getHeight();
                    CoinbaseData COINBASE_MESSAGE = coinbaseData(blockHeight + 1);

                    BlockMining blockMining = new BlockMining(db, chain.txDescIndex(), chain.getTxPool(), chain);
                    BlockTemplate blockTemplate = blockMining.getBlockTemplate(COINBASE_MESSAGE, minerAddress, params.getMaxBlockSize());

                    // Step 3 : Get block header
                    BlockHeader blockHeaderTemplate = blockTemplate.getBlockHeader(new Hash(bestBlockHash.getValue()));
                    long startTime = System.currentTimeMillis();
                    boolean blockFound = false;

                    // TODO : BUGBUG : Need to use chain.getDifficulty instead of using a fixed difficulty
                    long requiredHashCalulcations = 1024L;

                    // Try mining with the block template for five seconds.
                    int MINING_TRYAL_MILLIS = 1000;
                    // Step 3 : Loop until we find a block header hash less than the threshold.
                    long nonceValue = 0L;
                    do {
                        blockHeaderTemplate.setNonce(nonceValue);
                        BlockHeader miningBlockHeader = blockHeaderTemplate;
                        byte[] bashValue = HashUtil.hashBlockHeader(miningBlockHeader).getValue().getArray();

                        if (HashEstimation.getHashCalculations(bashValue) == requiredHashCalulcations) {

                            // Check the best block hash once more.
                            if (bestBlockHash.getValue().equals(chain.getBestBlockHash(db).getValue())) {
                                // Step 5 : When a block is found, create the block and put it on the blockchain.
                                // Also propate the block to the peer to peer network.
                                Block block = blockTemplate.createBlock(miningBlockHeader, miningBlockHeader.getNonce());
                                Hash blockHeaderHash = HashUtil.hashBlockHeader(block.getHeader());

                                BlockPropagator.propagate(blockHeaderHash, block);

                                blockFound = true;
                                logger.trace("Block Mined.\n hash : ${blockHeaderHash}\n\n");

                                listener.onCoinMined(block, minerAddress);
                            }
                        }

                        nonceValue++;

                    } while (!blockFound &&
                            System.currentTimeMillis() - startTime < MINING_TRYAL_MILLIS);
                } else {
                    logger.error("The best block hash is not defined yet.");
                }
                //}
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public void start() {
        thread.start();
    }

}
