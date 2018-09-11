package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Account;

public class AccountCodec implements  Codec<Account> {
    @Override
    public Account transcode(CodecInputOutputStream io, Account obj) {

        // As the account is used as a key in KeyValueDatabase,
        // we should not use codecs such as utf8_32 which prefixes the encoded data with the length of the data.
        // If any length of data is encoded, we can't compare string values on a KeyValueDatabase.

        // BUGBUG : Make sure it is ok to have utf8 for multi-byte languages such as Chinese or Korean.
        // If we are not doing any range search over the account name, it should be fine.
        String name = Codecs.CString.transcode(io, obj == null ? null : obj.getName());

        if (io.getInput()) {
            return new Account(name);
        }
        return null;
    }
}
