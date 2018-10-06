package com.rocketchain.chain.transaction;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.codec.primitive.Codecs;

public class CoinAddressCodec implements Codec<CoinAddress> {
    @Override
    public CoinAddress transcode(CodecInputOutputStream io, CoinAddress obj) {
        if (io.getInput()) {
            String address = Codecs.CString.transcode(io, null);
            return CoinAddress.from(address);
        } else {
            String base58Address = obj.base58();
            Codecs.CString.transcode(io, base58Address);
            return null;
        }
    }
}
