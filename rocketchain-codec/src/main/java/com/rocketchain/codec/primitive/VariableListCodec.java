package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

import java.util.ArrayList;
import java.util.List;

public class VariableListCodec<T> implements Codec<List<T>> {
    private Codec<Long> lengthCodec;
    private Codec<T> valueCodec;

    public VariableListCodec(Codec<Long> lengthCodec, Codec<T> valueCodec) {
        this.lengthCodec = lengthCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public List<T> transcode(CodecInputOutputStream io, List<T> obj) {
        Long valueLength = obj == null ? null : Long.valueOf(obj.size());
        Long length = io.transcode(lengthCodec, valueLength);
        if (io.getInput()) {
            List<T> mutableList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                T v = io.transcode(valueCodec, null);
                mutableList.add(v);
            }
            return mutableList;
        } else {
            assert (obj != null);
            assert (valueLength <= Integer.MAX_VALUE);
            for (int i = 0; i < valueLength; i++) {
                io.transcode(valueCodec, obj.get(i));
            }
            return null;
        }
    }

    public Codec<Long> getLengthCodec() {
        return lengthCodec;
    }

    public Codec<T> getValueCodec() {
        return valueCodec;
    }
}
