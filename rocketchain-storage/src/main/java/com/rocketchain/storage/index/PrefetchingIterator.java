package com.rocketchain.storage.index;


import com.rocketchain.codec.Codec;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class PrefetchingIterator<K, V> implements ClosableIterator<Pair<K, V>> {
    private ClosableIterator<Pair<byte[], byte[]>> iterator;

    private Pair<byte[], byte[]> elementToReturn;

    private byte[] prefix;

    private Codec<K> keyCodec;
    private Codec<V> valueCodec;

    /** We should stop the iteration if the prefix of the key changes.
     * So, hasNext first gets the next key and checks if the prefix remains unchanged.
     * next will return the element we got from hasNext.
     *
     * If the prefix is changed, we stop the iteration.
     *
     * @param iterator
     */
    public PrefetchingIterator(ClosableIterator<Pair<byte[], byte[]>> iterator, byte[] prefix, Codec<K> keyCodec, Codec<V> valueCodec) {
        this.iterator = iterator;
        this.prefix = prefix;
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public void close() {
        iterator.close();
    }

    @Override
    public boolean hasNext() {
        // We already have a prefetched key.
        if (elementToReturn != null) {
            byte[] rawKey = elementToReturn.getLeft();
            byte[] readPrefix = Arrays.copyOf(rawKey, prefix.length);
            if(Arrays.equals(readPrefix, prefix)) {
                return true;
            } else {
                return false;
            }
        } else {
            // We don't have a prefetched key. Prefetch one.
            if ( iterator.hasNext() ) {
                Pair<byte[], byte[]> pair = iterator.next();
                assert(pair.getLeft().length > 0);
                byte[] readPrefix = Arrays.copyOf(pair.getLeft(), prefix.length);
                elementToReturn = new MutablePair(pair.getLeft(), pair.getRight());

                // Continue the iteration only if the prefix remains unchanged.
                if(Arrays.equals(readPrefix, prefix)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public Pair<K, V> next() {
        assert (elementToReturn != null);

        byte[] rawKey = elementToReturn.getLeft();
        byte[] rawValue = elementToReturn.getRight();
        elementToReturn = null;

        byte[] readPrefix = Arrays.copyOf(rawKey, prefix.length);
        //val readPrefix = rawKey.take(prefix.size).toByteArray();
        // hasNext should return false if the prefix of the next key does not match the prefix.
        assert (Arrays.equals(readPrefix, prefix));
        // We need to drop the prefix byte for the rawKey.
        rawKey = Arrays.copyOfRange(rawKey, 1, rawKey.length);
        return new MutablePair<>(keyCodec.decode(rawKey) , valueCodec.decode(rawValue));
    }
}
