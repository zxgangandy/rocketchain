package com.rocketchain.storage.index;

import com.google.common.primitives.Bytes;
import com.rocketchain.codec.Codec;
import com.rocketchain.codec.primitive.CStringPrefixedCodec;
import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.CStringPrefixed;
import org.apache.commons.lang3.tuple.Pair;


public interface KeyValueDatabase {
    ClosableIterator<Pair<byte[], byte[]>> seek(byte[] keyOption);

    byte[] get(byte[] key);

    void put(byte[] key, byte[] value);

    void del(byte[] key);

    void close();

    TransactingKeyValueDatabase transacting();

    static byte[] prefixedKey(byte prefix, byte[] key) {
        return Bytes.concat(new byte[]{prefix}, key);
    }

    static byte[] prefixedKey(byte[] prefix, byte[] key) {
        return Bytes.concat(prefix, key);
    }

    default <V> V getObject(Codec<V> valueCodec, byte[] rawKey) {
        byte[] rawValue = get(rawKey);
        if (rawValue != null) {
            return valueCodec.decode(rawValue);
        } else {
            return null;
        }
    }

    default <K, V> V getObject(Codec<K> keyCodec, Codec<V> valueCodec, byte prefix, K key) {
        byte[] rawKey = prefixedKey(prefix, keyCodec.encode(key));
        return getObject(valueCodec, rawKey);
    }

    default <K,V> V getPrefixedObject(Codec<K> keyCodec , Codec<V> valueCodec , byte prefix , String keyPrefix , K key )  {
        byte[] rawKey = prefixedKey(prefix, new CStringPrefixedCodec<K>(keyCodec).encode(new CStringPrefixed(keyPrefix, key)) );
        return getObject(valueCodec, rawKey);
    }

    default <K, V> ClosableIterator<Pair<CStringPrefixed<K>, V>> seekPrefixedObject(Codec<K> keyCodec, Codec<V> valueCodec, byte prefix, String keyPrefix) {
        byte[] key = prefixedKey(prefix, Codecs.CString.encode(keyPrefix));

        return seekObjectInternal(new CStringPrefixedCodec(keyCodec), valueCodec, key, null);
    }

    default <K, V> ClosableIterator<Pair<CStringPrefixed<K>, V>> seekPrefixedObject(Codec<K> keyCodec, Codec<V> valueCodec, byte prefix) {
        byte[] keyPrefix = new byte[]{prefix};
        return seekObjectInternal(Codecs.cstringPrefixed(keyCodec), valueCodec, keyPrefix, null);
    }

    default <K, V> ClosableIterator<Pair<K, V>> seekObjectInternal(Codec<K> keyCodec, Codec<V> valueCodec, byte[] prefix, K keyOption) {
        byte[] seekKey;
        if (keyOption != null)
            seekKey = prefixedKey(prefix, keyCodec.encode(keyOption));
        else
            seekKey = prefix;

        ClosableIterator<Pair<byte[], byte[]>> rawIterator = seek(seekKey);

        return new PrefetchingIterator(rawIterator, prefix, keyCodec, valueCodec);
    }

    default <V> void putObject(Codec<V> valueCodec, byte[] rawKey, V value) {
        byte[] rawValue = valueCodec.encode(value);

        put(rawKey, rawValue);
    }

    default <K, V> void putObject(Codec<K> keyCodec, Codec<V> valueCodec, byte prefix, K key, V value) {
        byte[] rawKey = prefixedKey(prefix, keyCodec.encode(key));

        putObject(valueCodec, rawKey, value);
    }

    default <K, V> void putPrefixedObject(Codec<K> keyCodec, Codec<V> valueCodec, byte prefix, String keyPrefix, K key, V value) {
        byte[] rawKey = prefixedKey(prefix, new CStringPrefixedCodec<K>(keyCodec).encode(new CStringPrefixed(keyPrefix, key)));

        putObject(valueCodec, rawKey, value);
    }

    default <K> void delObject(Codec<K> keyCodec, byte prefix, K key) {
        byte[] rawKey = prefixedKey(prefix, keyCodec.encode(key));
        del(rawKey);
    }

    default <K> void delPrefixedObject(Codec<K> keyCodec, byte prefix, String keyPrefix, K key) {
        delPrefixedObject(keyCodec, prefix, new CStringPrefixed(keyPrefix, key));
    }

    default <K> void delPrefixedObject(Codec<K> keyCodec, byte prefix, CStringPrefixed<K> key) {
        byte[] rawKey = prefixedKey(prefix, new CStringPrefixedCodec<K>(keyCodec).encode(key));
        del(rawKey);
    }

}
