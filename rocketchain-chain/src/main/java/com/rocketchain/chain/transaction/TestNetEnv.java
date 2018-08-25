package com.rocketchain.chain.transaction;

import com.rocketchain.codec.BlockCodec;
import com.rocketchain.proto.*;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

import java.util.ArrayList;
import java.util.List;

public class TestNetEnv extends NetEnv {
    @Override
    public Hash getGenesisBlockHash() {
        return Hash.from("000000000933EA01AD0EE984209779BAAEC3CED90FA3F408719526F8D77F4943");
    }

    @Override
    public Block getGenesisBlock() {
//        String blockString = new StringBuilder()
//                .append("01 00 00 00 00 00 00 00")
//                .append("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00")
//                .append("00 00 00 00 00 00 00 00 00 00 00 00 3b a3 ed fd")
//                .append("7a 7b 12 b2 7a c7 2c 3e 67 76 8f 61 7f c8 1b c3")
//                .append("88 8a 51 32 3a 9f b8 aa 4b 1e 5e 4a da e5 49 4d")
//                .append("ff ff 00 1d 1a a4 ae 18 01 01 00 00 00 01 00 00")
//                .append("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00")
//                .append("00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff")
//                .append("ff ff 4d 04 ff ff 00 1d 01 04 45 54 68 65 20 54")
//                .append("69 6d 65 73 20 30 33 2f 4a 61 6e 2f 32 30 30 39")
//                .append("20 43 68 61 6e 63 65 6c 6c 6f 72 20 6f 6e 20 62")
//                .append("72 69 6e 6b 20 6f 66 20 73 65 63 6f 6e 64 20 62")
//                .append("61 69 6c 6f 75 74 20 66 6f 72 20 62 61 6e 6b 73")
//                .append("ff ff ff ff 01 00 f2 05 2a 01 00 00 00 43 41 04")
//                .append("67 8a fd b0 fe 55 48 27 19 67 f1 a6 71 30 b7 10")
//                .append("5c d6 a8 28 e0 39 09 a6 79 62 e0 ea 1f 61 de b6")
//                .append("49 f6 bc 3f 4c ef 38 c4 f3 55 04 e5 1e c1 12 de")
//                .append("5c 38 4d f7 ba 0b 8d 57 8a 4c 70 2b 6b f1 1d 5f")
//                .append("ac 00 00 00 00")
//                .toString();

        String blockString = "0100000000000000000000000000000000000000000000" +
                "000000000000000000000000003BA3EDFD7A7B12B27AC72C3E67768F617F" +
                "C81BC3888A51323A9FB8AA4B1E5E4A29AB5F49FFFF001D1DAC2B7C0101000" +
                "0000100000000000000000000000000000000000000000000000000000000" +
                "00000000FFFFFFFF4D04FFFF001D0104455468652054696D65732030332F4" +
                "A616E2F32303039204368616E63656C6C6F72206F6E206272696E6B206F66" +
                "207365636F6E64206261696C6F757420666F722062616E6B73FFFFFFFF010" +
                "0F2052A01000000434104678AFDB0FE5548271967F1A67130B7105CD6A828" +
                "E03909A67962E0EA1F61DEB649F6BC3F4CEF38C4F35504E51EC112D" +
                "E5C384DF7BA0B8D578A4C702B6BF11D5FAC00000000";

        //byte[] SERIALIZED_GENESIS_BLOCK = HexUtil.hexStringToByteArray(blockString);

//        BlockHeader blockHeader = new BlockHeader(1,
//                new Hash(Bytes.from("0000000000000000000000000000000000000000000000000000000000000000")),
//                new Hash(Bytes.from("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b")),
//                1231006505L,
//                486604799L,
//                2083236893L);
//
//        List<Transaction> transactions = new ArrayList<>();
//        List<TransactionInput> inputs = new ArrayList<>();
//
//        CoinbaseData coinbaseData = new CoinbaseData(Bytes.from("04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73"));
//
//        TransactionInput transactionInput = new GenerationTransactionInput(
//                new Hash(Bytes.from("0000000000000000000000000000000000000000000000000000000000000000")),
//                4294967295L,
//                coinbaseData,
//                4294967295L);
//
//        inputs.add(transactionInput);
//
//        List<TransactionOutput> outputs = new ArrayList<>();
//        LockingScript lockingScript = new LockingScript(Bytes.from("4104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac"));
//        TransactionOutput output = new TransactionOutput(5000000000L, lockingScript);
//        outputs.add(output);
//
//        Transaction transaction = new Transaction(1, inputs, outputs, 0L);
//        transactions.add(transaction);
//
//        Block block = new Block(blockHeader, transactions);
//        byte[] blockBytes = new BlockCodec().encode(block);

//        blockString = HexUtil.byteArrayToHexString(blockBytes);
//        System.out.println(blockString);

        return new BlockCodec().decode(HexUtil.hexStringToByteArray(blockString));
    }
}
