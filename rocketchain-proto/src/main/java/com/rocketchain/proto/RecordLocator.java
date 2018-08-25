package com.rocketchain.proto;

public class RecordLocator {
    private long offset;
    private int size;

    public RecordLocator(long offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }
}
