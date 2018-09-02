package com.rocketchain.storage;

import com.google.common.collect.Lists;
import com.rocketchain.codec.HashCodec;
import com.rocketchain.codec.OrphanTransactionDescriptorCodec;
import com.rocketchain.codec.primitive.OneByteCodec;
import com.rocketchain.proto.CStringPrefixed;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.OneByte;
import com.rocketchain.proto.OrphanTransactionDescriptor;
import com.rocketchain.storage.index.ClosableIterator;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.lang.HexUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Provides index operations for orphan transactions.
 */
public interface OrphanTransactionIndex {
    /** Put an orphan transaction
     *
     * @param hash The hash of the transaction header.
     * @param orphanTransactionDescriptor The descriptor of the orphan transaction.
     */
    default void putOrphanTransaction(KeyValueDatabase db , Hash hash , OrphanTransactionDescriptor orphanTransactionDescriptor )  {
        db.putObject(new HashCodec(), new OrphanTransactionDescriptorCodec(), DB.ORPHAN_TRANSACTION, hash, orphanTransactionDescriptor);
    }

    /** Get an orphan transaction by the hash of it.
     *
     * @param hash The orphan transaction header.
     * @return Some(transaction) if an orphan transaction was found by the hash. None otherwise.
     */
    default OrphanTransactionDescriptor getOrphanTransaction(KeyValueDatabase db, Hash hash )  {
        return db.getObject(new HashCodec(),  new OrphanTransactionDescriptorCodec(), DB.ORPHAN_TRANSACTION, hash);
    }

    /** Delete a specific orphan transaction.
     *
     * @param hash The hash of the orphan transaction.
     */
    default void delOrphanTransaction(KeyValueDatabase db , Hash hash ) {
        db.delObject(new HashCodec(), DB.ORPHAN_TRANSACTION, hash);
    }

    /** Add an orphan transaction than depends on a transaction denoted by the hash.
     *
     * @param missingTransactionHash The hash of the missing transaction that the orphan transaction depends on.
     * @param orphanTransactionHash The hash of the orphan transaction.
     */
    default void addOrphanTransactionByParent(KeyValueDatabase db , Hash missingTransactionHash , Hash orphanTransactionHash )  {
        // TODO : Optimize : Reduce the length of the prefix string by using base64 encoding?
        db.putPrefixedObject(new HashCodec(), new OneByteCodec(), DB.ORPHAN_TRANSACTIONS_BY_DEPENDENCY,
                HexUtil.byteArrayToHexString(missingTransactionHash.getValue().getArray()), orphanTransactionHash, new OneByte((byte)1) );
    }

    /** Get all orphan transactions that depend on the given transaction.
     *
     * @param missingTransactionHash The hash of the missing transaction that the orphan transaction depends on.
     * @return Hash of all orphan transactions that depend on the given missing transaction.
     */
    default List<Hash> getOrphanTransactionsByParent(KeyValueDatabase db , Hash missingTransactionHash )  {
        ClosableIterator<Pair<CStringPrefixed<Hash>, OneByte>> iterator = db.seekPrefixedObject(new HashCodec(),
                new OneByteCodec(), DB.ORPHAN_TRANSACTIONS_BY_DEPENDENCY,
                HexUtil.byteArrayToHexString(missingTransactionHash.getValue().getArray()));

        List<Hash> list = Lists.newArrayList();
        try {
            // BUGBUG : Change the code not to use Pair, but a data class. This is code so hard to read.

            while (iterator.hasNext()) {
                Pair<CStringPrefixed<Hash>, OneByte> pair = iterator.next();
                CStringPrefixed<Hash> cstringPrefixed = pair.getKey();
                Hash hash = cstringPrefixed.getData();

                list.add(hash);
            }

            return list;
        } finally {
            iterator.close();
        }
    }

    /** Del all orphan transactions that depend on the given missing transaction.
     *
     * @param missingTransactionHash The hash of the missing transaction that the orphan transactions depend on.
     */
    default void delOrphanTransactionsByParent(KeyValueDatabase db , Hash missingTransactionHash )  {
        getOrphanTransactionsByParent(db, missingTransactionHash).stream().forEach(transactionHash->{
            db.delPrefixedObject(new HashCodec(), DB.ORPHAN_TRANSACTIONS_BY_DEPENDENCY, HexUtil.byteArrayToHexString(
                    missingTransactionHash.getValue().getArray()), transactionHash);
        });
    }
}
