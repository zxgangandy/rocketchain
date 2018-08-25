package com.rocketchain.storage.record;

import java.io.File;

public class BlockRecordStorage extends RecordStorage{
    private final static String FILE_PREFIX = "blk";
    private File directoryPath;
    private int maxFileSize;

    public BlockRecordStorage(File directoryPath, int maxFileSize) {
        super(directoryPath, FILE_PREFIX, maxFileSize);
        this.directoryPath = directoryPath;
        this.maxFileSize = maxFileSize;
    }

    public File getDirectoryPath() {
        return directoryPath;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }
}
