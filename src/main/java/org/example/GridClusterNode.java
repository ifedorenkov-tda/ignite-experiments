package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.ContinuousQueryWithTransformer;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteClosure;
import org.example.cache.CacheAccountKey;
import org.example.cache.Caches;
import org.example.config.GridClusterNodeConfig;
import org.example.domain.Account;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryEvent;

public class GridClusterNode {
    public static void main(String[] args) throws Throwable {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GridClusterNodeConfig.class);
        IgniteConfiguration igniteConfiguration = context.getBean(IgniteConfiguration.class);

        QueryCursor<Cache.Entry<CacheAccountKey, Account>> queryCursor = null;
        try (Ignite ignite = Ignition.getOrStart(igniteConfiguration)) {
            logger(ignite).info("Started cluster node!");

            System.in.read();
        } finally {
            if (queryCursor != null) {
                queryCursor.close();
            }
        }
    }

    private static IgniteLogger logger(Ignite ignite) {
        return ignite.log().getLogger(GridClusterNode.class);
    }

    private static void onUpdatedAccount(Iterable<? extends String> names) {
        for (String name : names) {
            System.out.println("Updated: " + name);
        }
    }

    public static class AccountChangedEventRmtTransformer implements
            IgniteClosure<CacheEntryEvent<? extends CacheAccountKey, ? extends Account>, String>
    {
        private static final long serialVersionUID = 0;

        @Override
        public String apply(CacheEntryEvent<? extends CacheAccountKey, ? extends Account> cacheEntryEvent) {
            Account value = cacheEntryEvent.getValue();
            return value != null ? value.getName() : "";
        }
    }
}
