package com.rocketchain.client.test;

import com.rocketchain.codec.BlockCodec;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.*;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TestCodec {

    @Test
    public void testBlockEncode() {
        BlockHeader blockHeader = new BlockHeader(1,
                new Hash(Bytes.from("0000000000000000000000000000000000000000000000000000000000000000")),
                new Hash(Bytes.from("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b")),
                1231006505L,
                486604799L,
                2083236893L);

        List<Transaction> transactions = new ArrayList<>();
        List<TransactionInput> inputs = new ArrayList<>();

        CoinbaseData coinbaseData = new CoinbaseData(Bytes.from("04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73"));

        TransactionInput transactionInput = new GenerationTransactionInput(
                new Hash(Bytes.from("0000000000000000000000000000000000000000000000000000000000000000")),
                4294967295L,
                coinbaseData,
                4294967295L);

        inputs.add(transactionInput);

        List<TransactionOutput> outputs = new ArrayList<>();
        LockingScript lockingScript = new LockingScript(Bytes.from("4104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac"));
        TransactionOutput output = new TransactionOutput(5000000000L, lockingScript);
        outputs.add(output);

        Transaction transaction = new Transaction(1, inputs, outputs, 0L);
        transactions.add(transaction);

        Block block = new Block(blockHeader, transactions);

        System.out.println("encode=> " +block);

        byte[] blockBytes = new BlockCodec().encode(block);

        String blockString = HexUtil.byteArrayToHexString(blockBytes);
        System.out.println(blockString);
    }

    @Test
    public void testBlockDecode() {
        String blockString = "01000000000000000000000000000000000000000000000000000000000000000000" +
                "00003BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A29AB5F49FFFF0" +
                "01D1DAC2B7C0101000000010000000000000000000000000000000000000000000000000000000000" +
                "000000FFFFFFFF4D04FFFF001D0104455468652054696D65732030332F4A616E2F323030392043686" +
                "16E63656C6C6F72206F6E206272696E6B206F66207365636F6E64206261696C6F757420666F722062" +
                "616E6B73FFFFFFFF0100F2052A01000000434104678AFDB0FE5548271967F1A67130B7105CD6A828E" +
                "03909A67962E0EA1F61DEB649F6BC3F4CEF38C4F35504E51EC112DE5C384DF7BA0B8D578A4C702B6B" +
                "F11D5FAC00000000";
        Block block = new BlockCodec().decode(HexUtil.hexStringToByteArray(blockString));

        System.out.println("decode=> " + block);

        System.out.println("header hash=> " + HashUtil.hashBlockHeader(block.getHeader()));
    }

}
