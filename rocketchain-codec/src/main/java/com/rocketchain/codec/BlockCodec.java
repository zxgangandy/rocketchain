package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.Block;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.Transaction;

import java.util.List;

public class BlockCodec extends ProtocolMessageCodec<Block> {


    public void setCommand() {
        command = "block";
    }

    public void setClazz() {
        clazz = Block.class;
    }

    private VariableListCodec<Transaction> TransactionListCodec = Codecs.variableListOf(new TransactionCodec());

    @Override
    public Block transcode(CodecInputOutputStream io, Block obj) {

        BlockHeader header = new BlockHeaderCodec().transcode(io, obj == null ? null : obj.getHeader());
        List<Transaction> transactions = TransactionListCodec.transcode(io, obj == null ? null : obj.getTransactions());

        if (io.getInput()) {
            return new Block(header, transactions);
        }
        return null;
    }
}
