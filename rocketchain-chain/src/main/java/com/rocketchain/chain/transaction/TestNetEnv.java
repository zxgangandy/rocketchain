package com.rocketchain.chain.transaction;

import com.rocketchain.codec.BlockCodec;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.Hash;
import com.rocketchain.utils.lang.HexUtil;

public class TestNetEnv extends NetEnv {

    @Override
    public Hash getGenesisBlockHash() {
        return Hash.from("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");
    }

    @Override
    public Block getGenesisBlock() {

        String blockHex = new StringBuilder()
                .append("0100000000000000")
                .append("00000000000000000000000000000000")
                .append("0000000000000000000000003ba3edfd")
                .append("7a7b12b27ac72c3e67768f617fc81bc3")
                .append("888a51323a9fb8aa4b1e5e4adae5494d")
                .append("ffff001d1aa4ae180101000000010000")
                .append("00000000000000000000000000000000")
                .append("0000000000000000000000000000ffff")
                .append("ffff4d04ffff001d0104455468652054")
                .append("696d65732030332f4a616e2f32303039")
                .append("204368616e63656c6c6f72206f6e2062")
                .append("72696e6b206f66207365636f6e642062")
                .append("61696c6f757420666f722062616e6b73")
                .append("ffffffff0100f2052a01000000434104")
                .append("678afdb0fe5548271967f1a67130b710")
                .append("5cd6a828e03909a67962e0ea1f61deb6")
                .append("49f6bc3f4cef38c4f35504e51ec112de")
                .append("5c384df7ba0b8d578a4c702b6bf11d5f")
                .append("ac00000000").toString();

        return new BlockCodec().decode(HexUtil.hexStringToByteArray(blockHex));
    }
}
