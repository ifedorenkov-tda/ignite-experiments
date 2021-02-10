package org.example.domain;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.example.cache.Caches;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CacheAccountDao implements AccountDao {
    private final Ignite ignite;

    public CacheAccountDao(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public Collection<Account> findAll() {
        IgniteCache<BinaryObject, BinaryObject> cache = ignite.cache(Caches.ACCOUNT.getName()).withKeepBinary();
        SqlFieldsQuery query = new SqlFieldsQuery("select * from Account");
        try (FieldsQueryCursor<List<?>> cursor = cache.query(query)) {
            for (List<?> row : cursor) {
                System.out.println(row);
            }
        }
        return Collections.emptyList();
    }
}
