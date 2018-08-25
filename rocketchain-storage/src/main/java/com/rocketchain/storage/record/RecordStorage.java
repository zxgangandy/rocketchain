package com.rocketchain.storage.record;

import com.rocketchain.codec.Codec;
import com.rocketchain.proto.FileRecordLocator;
import com.rocketchain.proto.RecordLocator;
import com.rocketchain.storage.exception.BlockStorageException;
import com.rocketchain.utils.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Maintains a list of record files.
 *
 * Why?
 *   A record file has a maximum size. If a file reaches the maximum size, we need to add a new record file.
 *   Record storage keeps track of multiple record files, enables us to search a record by a record locator,
 *   which hash the file index of the multiple record files.
 */
public class RecordStorage {
    private final Logger logger = LoggerFactory.getLogger(RecordStorage.class);

    private File directoryPath;
    private String filePrefix;
    private long maxFileSize;
    private List<RecordFile> files = new ArrayList<>();

    public RecordStorage(File directoryPath, String filePrefix, long maxFileSize) {
        this.directoryPath = directoryPath;
        this.filePrefix = filePrefix;
        this.maxFileSize = maxFileSize;


        if (directoryPath.exists()) {
            // For each file in the path
            File[] fileList = directoryPath.listFiles();
            if (fileList.length == 0) {
                // Do nothing. no file exists
            } else {
                Arrays.sort(fileList, Comparator.comparing((File file)->file.getName()));

                for ( File file : fileList) {
                    BlockFileName decodedFileName = BlockFileName.from(file.getName());
                    if (decodedFileName == null) {
                        // Ignore files that are not matching the block file name format.
                    } else {
                        if (decodedFileName.getPrefix().equals(filePrefix)) {
                            if (files.size() == decodedFileName.getFileNumber()) {
                                files.add( newFile(file) );
                            } else {
                                logger.error("Invalid Block File Number. Expected : "+ files.size()
                                        +", Actual : " + decodedFileName.getFileNumber());
                                throw new BlockStorageException(ErrorCode.InvalidFileNumber);
                            }
                        }
                    }
                }
            }
        } else {
            throw new BlockStorageException(ErrorCode.BlockFilePathNotExists);
        }

        /** If there is no file at all, add a file.
         */
        if (files.isEmpty()) {
            files.add(newFile());
        }
    }

    public RecordFile lastFile() {
         return files.get(lastFileIndex());
    }

    /** Because we are appending records, flushing the last file is enough.
     * We also flush the last file when a file is added, so flushing the current last file is enough.
     */
    public void flush() {
        lastFile().flush();
    }

    public int lastFileIndex(){
        return files.size()-1;
    }

    public RecordFile newFile(File blockFile ) {
        try {
            return  new RecordFile(blockFile, maxFileSize );
        } catch (IOException e) {
            return null;
        }
    }

    public RecordFile newFile()  {
        int fileNumber = lastFileIndex() + 1;
        File blockFile = new  File( directoryPath.getAbsolutePath() + File.separatorChar
                + new BlockFileName(filePrefix, fileNumber));
        return newFile(blockFile);
    }

    /** Add a file. Also flush the last file, so that we do not lose any file contents when the system crashes.
     */
    public void addNewFile() {
        lastFile().flush();
        files.add( newFile() );
    }

    // TODO : Make FileRecordLocator to have a type parameter, T so that we can have a compile
    // error when an incorrect codec is used for a record locator.

    public <T> FileRecordLocator appendRecord(Codec<T> codec , T record )   {
        try {
            RecordLocator recordLocator = lastFile().appendRecord(codec, record);
            return new FileRecordLocator( lastFileIndex(), recordLocator );
        } catch( BlockStorageException e  ) {
            if (e.getCode() == ErrorCode.OutOfFileSpace) {
                addNewFile();
                RecordLocator recordLocator = lastFile().appendRecord(codec, record);
                return new FileRecordLocator( lastFileIndex(), recordLocator );
            } else {
                throw e;
            }
        }
    }

    public <T> T readRecord(Codec<T> codec , FileRecordLocator locator )  {
        if (locator.getFileIndex() < 0 || locator.getFileIndex() >= files.size()) {
            throw new BlockStorageException(ErrorCode.InvalidFileNumber);
        }

        RecordFile file = files.get(locator.getFileIndex());

        return file.readRecord(codec, locator.getRecordLocator());
    }

    public void close() {
        // Flush the last file first not to lose any data.
        lastFile().flush();

        for (RecordFile file : files) {
            file.flush();
            file.close();
        }
    }

    public List<RecordFile> getFiles() {
        return files;
    }
}
