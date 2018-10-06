package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.OwnershipDescriptor;

import java.util.List;

public class OwnershipDescriptorCodec implements Codec<OwnershipDescriptor> {

    private VariableListCodec<String> StringListCodec = Codecs.variableListOf(Codecs.VariableString);

    @Override
    public OwnershipDescriptor transcode(CodecInputOutputStream io, OwnershipDescriptor obj) {

        String account = Codecs.VariableString.transcode(io, obj == null ? null : obj.getAccount());
        List<String> privateKeys = StringListCodec.transcode(io, obj == null ? null : obj.getPrivateKeys());

        if (io.getInput()) {
            return new OwnershipDescriptor(account, privateKeys);
        }
        return null;
    }
}
