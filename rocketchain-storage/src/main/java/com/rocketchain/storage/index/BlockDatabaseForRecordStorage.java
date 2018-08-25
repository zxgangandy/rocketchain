package com.rocketchain.storage.index;

import com.rocketchain.codec.BlockFileInfoCodec;
import com.rocketchain.codec.FileNumberCodec;
import com.rocketchain.proto.BlockFileInfo;
import com.rocketchain.proto.FileNumber;
import com.rocketchain.storage.BlockDatabase;
import com.rocketchain.storage.DB;

/**
 * BlockDatabase for use with RecordStorage.
 * <p>
 * Additional features : tracking block file info
 * <p>
 * When storing blocks with RecordStorage, we need to keep track of block file information.
 */
public interface BlockDatabaseForRecordStorage extends BlockDatabase {

    default void putBlockFileInfo(KeyValueDatabase db, FileNumber fileNumber, BlockFileInfo blockFileInfo) {
        // Input validation for the block file info.
        BlockFileInfo currentInfoOption = getBlockFileInfo(db, fileNumber);
        if (currentInfoOption != null) {
            BlockFileInfo currentInfo = currentInfoOption;
            // Can't put the same block info twice.
            assert (currentInfo != blockFileInfo);

            // First block height can't be changed.
            assert (currentInfo.getFirstBlockHeight() == blockFileInfo.getFirstBlockHeight());

            // First block timestamp can't be changed.
            assert (currentInfo.getFirstBlockTimestamp() == blockFileInfo.getFirstBlockTimestamp());

            // Block count should not be decreased.
            // Block count should increase
            assert (currentInfo.getBlockCount() < blockFileInfo.getBlockCount());

            // File size should not be decreased
            // File size should increase
            assert (currentInfo.getFileSize() < blockFileInfo.getFileSize());


            // The last block height should not be decreased.
            // The last block height should increase

            // when a orphan block is
            //      assert( currentInfo.lastBlockHeight < blockFileInfo.lastBlockHeight)

            // Caution : The last block timestamp can decrease.
        }

        db.putObject(new FileNumberCodec(), new BlockFileInfoCodec(), DB.BLOCK_FILE_INFO, fileNumber, blockFileInfo);
    }

    default BlockFileInfo getBlockFileInfo(KeyValueDatabase db, FileNumber fileNumber) {
        return db.getObject(new FileNumberCodec(), new BlockFileInfoCodec(), DB.BLOCK_FILE_INFO, fileNumber);
    }

    default void putLastBlockFile(KeyValueDatabase db, FileNumber fileNumber) {
        // Input validation check for the fileNumber.
        FileNumber fileNumberOption = getLastBlockFile(db);
        if (fileNumberOption != null) {
            // The file number should increase.
            assert (fileNumberOption.getFileNumber() < fileNumber.getFileNumber());
        }

        db.putObject(new FileNumberCodec(), new byte[]{DB.LAST_BLOCK_FILE}, fileNumber);
    }

    default FileNumber getLastBlockFile(KeyValueDatabase db) {
        return db.getObject(new FileNumberCodec(), new byte[]{DB.LAST_BLOCK_FILE});
    }


}
