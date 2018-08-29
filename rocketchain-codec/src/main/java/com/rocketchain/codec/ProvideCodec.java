package com.rocketchain.codec;

public class ProvideCodec<T> implements Codec<T> {
    private T objectSample;

    public ProvideCodec(T objectSample) {
        this.objectSample = objectSample;
    }

    @Override
    public T transcode(CodecInputOutputStream io, T obj) {
        if (io.getInput()) {
            return objectSample;
        } else {
            return null;
        }
    }
}
