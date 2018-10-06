package com.rocketchain.chain.transaction;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.codec.LockingScriptCodec;
import com.rocketchain.proto.LockingScript;

public class ParsedPubKeyScriptCodec implements Codec<ParsedPubKeyScript> {
    @Override
    public ParsedPubKeyScript transcode(CodecInputOutputStream io, ParsedPubKeyScript obj) {
        if (io.getInput()) {
            LockingScript lockingScript = new LockingScriptCodec().transcode(io, null);
            return ParsedPubKeyScript.from(lockingScript);
        } else {
            LockingScript lockingScript = obj.lockingScript();
            new LockingScriptCodec().transcode(io, lockingScript);
            return null;
        }
    }
}
