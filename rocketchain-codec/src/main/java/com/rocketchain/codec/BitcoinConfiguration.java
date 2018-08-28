package com.rocketchain.codec;

import com.rocketchain.proto.Magic;

public class BitcoinConfiguration {
    private Magic magic ;

    public static BitcoinConfiguration config = new  BitcoinConfiguration(Magic.MAIN);

    public BitcoinConfiguration(Magic magic) {
        this.magic = magic;
    }

    public Magic getMagic() {
        return magic;
    }
}
