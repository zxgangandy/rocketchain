package com.rocketchain.net.message;

import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Inv;
import com.rocketchain.proto.InvType;
import com.rocketchain.proto.InvVector;

import java.util.List;
import java.util.stream.Collectors;

public class InvFactory {
    /**
     * Create an Inv message containing block inventories.
     *
     * @param blockHashes The hash of blocks to put into the block inventories.
     * @return The created Inv message.
     */
    public static Inv createBlockInventories(List<Hash> blockHashes) {

        return new Inv(blockHashes.stream()
                .map(hash ->
                        new InvVector(
                                InvType.MSG_BLOCK,
                                hash)
                ).collect(Collectors.toList()));
    }

    /**
     * Create an Inv message containing transaction inventories.
     *
     * @param transactionHashes The hash of transactions to put into the transation inventories.
     * @return The created Inv message.
     */
    public static Inv createTransactionInventories(List<Hash> transactionHashes) {
        return new Inv(transactionHashes.stream()
                .map(hash ->
                        new InvVector(
                                InvType.MSG_TX,
                                hash)
                ).collect(Collectors.toList()));
    }
}
