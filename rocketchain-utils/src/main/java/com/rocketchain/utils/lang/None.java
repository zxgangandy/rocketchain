package com.rocketchain.utils.lang;

public class None<T> extends Option<T> {

    @Override
    public int hashCode() {
        return 1258712095;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof None;
    }

    @Override
    public T toNullable() {
        return null;
    }
}
