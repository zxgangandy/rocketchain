package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.FileRecordLocator;
import com.rocketchain.proto.RecordLocator;

public class FileRecordLocatorCodec implements Codec<FileRecordLocator> {
    @Override
    public FileRecordLocator transcode(CodecInputOutputStream io, FileRecordLocator obj) {
        Integer fileIndex     = Codecs.Int32L.transcode(io, obj == null ? null : obj.getFileIndex());
        RecordLocator recordLocator = new RecordLocatorCodec().transcode(io, obj == null ? null : obj.getRecordLocator());

        if (io.getInput()) {
            return new FileRecordLocator(fileIndex, recordLocator);
        }
        return null;
    }
}
