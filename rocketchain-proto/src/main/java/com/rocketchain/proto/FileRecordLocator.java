package com.rocketchain.proto;

public class FileRecordLocator {

    private int fileIndex; ;
    private RecordLocator recordLocator;

    public FileRecordLocator(int fileIndex, RecordLocator recordLocator ) {
        this.fileIndex = fileIndex;
        this.recordLocator = recordLocator;
    }

    public void setRecordLocator(RecordLocator recordLocator) {
        this.recordLocator = recordLocator;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public RecordLocator getRecordLocator() {
        return recordLocator;
    }
}
