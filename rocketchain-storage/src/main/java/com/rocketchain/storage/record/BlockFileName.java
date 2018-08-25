package com.rocketchain.storage.record;

public class BlockFileName {
    private final static int PREFIX_LENGTH = 3;
    private final static String POSTFIX = ".dat";

    private String prefix;
    private int fileNumber;

    public BlockFileName(String prefix, int fileNumber) {
        this.prefix = prefix;
        this.fileNumber = fileNumber;
    }

    @Override
    public String toString() {
        return prefix + String.format("%05d", fileNumber) + POSTFIX;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    /**
     * Extract prefix and file number from the given string.
     * This method is required for pattern matching a file name with match statement in Scala.
     * <p>
     * Example> The following code prints "prefix : blk, fileNumber : 1"
     * <p>
     * "blk00001.dat" match {
     * case BlockFileName(prefix, fileNumber) => {
     * println(s"prefix : $prefix, fileNumber : $fileNumber")
     * }
     * }
     *
     * @param fileName The file name, where we extract the prefix and the file number.
     * @return Some of (prefix, file number) pair, if the given file name matches the pattern. None otherwise.
     */
    public static BlockFileName from(String fileName) {
        if (fileName.endsWith(POSTFIX)) {
            String prefix = fileName.substring(0, PREFIX_LENGTH);

            String fileNumberPart =
                    fileName.substring(
                            PREFIX_LENGTH, // start offset - inclusive
                            fileName.length() - POSTFIX.length()); // end offset - exclusive

            return new BlockFileName(prefix, Integer.parseInt(fileNumberPart));
        } else {
            return null;
        }
    }
}

