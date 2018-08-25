package com.rocketchain.proto;

public class CStringPrefixed<T> {
    private String prefix;
    private T data;

    public CStringPrefixed(String prefix , T data) {
        this.prefix = prefix;
        this.data = data;
    }

    public String getPrefix() {
        return prefix;
    }

    public T getData() {
        return data;
    }
}
