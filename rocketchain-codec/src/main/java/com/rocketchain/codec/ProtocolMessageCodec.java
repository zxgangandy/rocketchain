package com.rocketchain.codec;

public abstract class ProtocolMessageCodec<T> implements Codec<T> {

    protected String command;
    protected Class clazz;

}
