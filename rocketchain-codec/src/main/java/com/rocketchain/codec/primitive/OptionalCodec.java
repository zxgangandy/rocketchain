package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.utils.lang.None;
import com.rocketchain.utils.lang.Option;
import com.rocketchain.utils.lang.Some;

public class OptionalCodec<T> implements Codec<Option<T>> {
    private Codec<Boolean> flagCodec;
    private Codec<T> valueCodec;

    public OptionalCodec(Codec<Boolean> flagCodec, Codec<T> valueCodec) {
        this.flagCodec = flagCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public Option<T> transcode(CodecInputOutputStream io, Option<T> obj) {
        if (io.getInput()) {
            boolean hasValue = flagCodec.transcode(io, null);
            if (hasValue) {
                return new Some(valueCodec.transcode(io, null));
            } else {
                return new None();
            }
        } else {
            Option optionObject = obj;

            if (optionObject instanceof None) {
                flagCodec.transcode(io, true);
            } else if (optionObject instanceof Some) {
                flagCodec.transcode(io, true);
                valueCodec.transcode(io, (T)((Some) optionObject).getValue());
            }
        }
        return null;
    }
}
