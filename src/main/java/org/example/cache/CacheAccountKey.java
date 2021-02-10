package org.example.cache;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class CacheAccountKey {
    @AffinityKeyMapped
    private AffinityAccountKey affinityAccountKey;

    public CacheAccountKey(long accountId) {
        affinityAccountKey = new AffinityAccountKey(accountId);
    }

    public CacheAccountKey() {
    }

    public AffinityAccountKey getAffinityAccountKey() {
        return affinityAccountKey;
    }

    @Override
    public String toString() {
        return "CacheAccountKey{" +
                "affinityAccountKey=" + affinityAccountKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheAccountKey that = (CacheAccountKey) o;

        return affinityAccountKey.equals(that.affinityAccountKey);
    }

    @Override
    public int hashCode() {
        return affinityAccountKey.hashCode();
    }
}
