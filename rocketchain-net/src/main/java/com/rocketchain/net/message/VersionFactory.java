package com.rocketchain.net.message;

import com.rocketchain.chain.Blockchain;
import com.rocketchain.proto.IPv6Address;
import com.rocketchain.proto.NetworkAddress;
import com.rocketchain.proto.Version;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.lang.Bytes;

import java.math.BigInteger;

public class VersionFactory {
    public  static Version create()  {
        KeyValueDatabase db  = Blockchain.get().getDb();

        // RocketChain uses Long type for the block height, but the Version.startHeight is encoded in 32bit little endian integer.
        // If we create two blocks a second, it takes about 15 years to fill up
        long bestBlockHeight = Blockchain.get().getBestBlockHeight();
        assert(bestBlockHeight <= Integer.MAX_VALUE);

        return new Version(70002, new BigInteger("1"), 1454059080L, new  NetworkAddress(new BigInteger("1"),
                new IPv6Address(Bytes.from("00000000000000000000ffff00000000")), 0), new NetworkAddress(new BigInteger("1"),
                new IPv6Address(Bytes.from("00000000000000000000ffff00000000")), 8333), new BigInteger("5306546289391447548"),
                "/Satoshi:0.11.2/", bestBlockHeight, true);
    }

}
