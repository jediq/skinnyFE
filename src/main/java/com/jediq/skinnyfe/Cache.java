package com.jediq.skinnyfe;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cache time is in milliseconds
 *
 * Zero cache time means no caching
 */

public class Cache<K, V> {

    private Map<String, TimestampedSoftReference<V>> cacheMap = new HashMap<>();
    private final long cacheTime;

    public Cache(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public Cache() {
        this(0);
    }

    public synchronized V item(K key, Generator<V> generator) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        TimestampedSoftReference<V> reference = cacheMap.get(wrapKey(key));
        if (reference == null
                || reference.get() == null
                || reference.getCreationTime() < System.currentTimeMillis() - cacheTime) {
            reference = new TimestampedSoftReference<>(generator.generate());
            cacheMap.put(wrapKey(key), reference);
        }
        return reference.get();
    }

    public void clear(K key) {
        cacheMap.remove(wrapKey(key));
    }

    public void clear() {
        cacheMap.clear();
    }

    public String wrapKey(K key) {
        return key.toString();
    }

    public Set<V> itemSet() {
        return cacheMap.values().stream().filter(i -> i != null).map(SoftReference::get).collect(Collectors.toSet());
    }

    public interface Generator <V> {
        V generate();
    }

    public class TimestampedSoftReference <T> extends SoftReference <T> {

        private final long creationTime;

        public TimestampedSoftReference(T referent) {
            super(referent);
            this.creationTime = System.currentTimeMillis();
        }

        public long getCreationTime() {
            return creationTime;
        }
    }
}