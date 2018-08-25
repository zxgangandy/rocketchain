package com.rocketchain.storage.index;

import com.rocketchain.codec.HashCodec;
import com.rocketchain.codec.primitive.LongValueCodec;
import com.rocketchain.codec.primitive.OneByteCodec;
import com.rocketchain.proto.CStringPrefixed;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.LongValue;
import com.rocketchain.proto.OneByte;
import com.rocketchain.storage.DB;
import com.rocketchain.utils.Base58Util;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Maintains an index from the creation time of a transaction to the transaction hash.
 */
public interface TransactionTimeIndex {



    default String timeToString(long nanoSeconds) {
        int MaxBase58EncodedLength = Base58Util.encode(new LongValueCodec().encode(new LongValue(Long.MAX_VALUE))).length();
        String encodedString = Base58Util.encode(new LongValueCodec().encode(new LongValue(nanoSeconds)));

        if (encodedString.length() > MaxBase58EncodedLength) {
            assert (false);
            return "";
        } else if (encodedString.length() == MaxBase58EncodedLength) {
            return encodedString;
        } else {
            // Prefix the base58 encoded string with "1", to make the encoded string take the MaxBase58EncodedLength bytes.
            // This is necessary to sort transactions by transaction time.
            byte[] arr = new byte[MaxBase58EncodedLength - encodedString.length()];
            Arrays.fill(arr, (byte)1);

            String prefix = new String(arr);
            return prefix + encodedString;
        }
    }


    default byte getTxTimePrefix() {
        return DB.TRANSACTION_TIME;
    }

    /**
     * Put a transaction into the transaction time index.
     *
     * @param creationTime The time when the transaction was created (in nano seconds)
     * @param txHash       The hash of the transaction to add
     */
    default void putTransactionTime(KeyValueDatabase db, long creationTime, Hash txHash) {
        //logger.trace(s"putTransactionDescriptor : ${txHash}")

        String keyPrefix = timeToString(creationTime);

        db.putPrefixedObject(new HashCodec(), new OneByteCodec(), getTxTimePrefix(), keyPrefix, txHash, new OneByte((byte) 0));
    }

    /**
     * Get a transaction from the transaction pool.
     *
     * @param count The number of hashes to get.
     * @return The transaction which matches the given transaction hash.
     */
    default List<CStringPrefixed<Hash>> getOldestTransactionHashes(KeyValueDatabase db, int count)

    {
        assert (count > 0);
        //logger.trace(s"getTransactionFromPool : ${txHash}")
        ClosableIterator<Pair<CStringPrefixed<Hash>, OneByte>> iterator = db.seekPrefixedObject(new HashCodec(), new OneByteCodec(), getTxTimePrefix());
        try {
            List<CStringPrefixed<Hash>> buffer = new ArrayList<>();
            int copied = 0;
            while (copied < count && iterator.hasNext()) {

                Pair<CStringPrefixed<Hash>, OneByte> pair = iterator.next();
                buffer.add(pair.getLeft());
                copied += 1;
            }
            return buffer;
        } finally {
            iterator.close();
        }
    }

    /**
     * Del a transaction from the pool.
     *
     * @param creationTime The time when the transaction was created (in nano seconds)
     * @param txHash       The hash of the transaction to remove
     */
    default void delTransactionTime(KeyValueDatabase db, long creationTime, Hash txHash) {
        String keyPrefix = timeToString(creationTime);

        db.delPrefixedObject(new HashCodec(), getTxTimePrefix(), keyPrefix, txHash);
    }

    default void delTransactionTime(KeyValueDatabase db, CStringPrefixed<Hash> key) {
        db.delPrefixedObject(new HashCodec(), getTxTimePrefix(), key);
    }


}
