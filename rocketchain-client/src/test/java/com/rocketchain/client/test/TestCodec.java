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
                1296688602L,
                486604799L,
                414098458L);

        List<Transaction> transactions = new ArrayList<>();
        List<TransactionInput> inputs = new ArrayList<>();

        CoinbaseData coinbaseData = new CoinbaseData(Bytes.from("04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73"));

        TransactionInput transactionInput = new GenerationTransactionInput(
                new Hash(Bytes.from("0000000000000000000000000000000000000000000000000000000000000000")),
                4294967295L,//16 进制： FFFFFFFF
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
        String blockString = "0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4adae5494dffff001d1aa4ae180101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000";
        Block block = new BlockCodec().decode(HexUtil.hexStringToByteArray(blockString));

        System.out.println("decode=> " + block);

        System.out.println("header hash=> " + HashUtil.hashBlockHeader(block.getHeader()));
    }

}
