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
import org.example.config.GridClientNodeConfig;
import org.example.domain.Account;
import org.example.domain.DomainAccountKey;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryEvent;
import java.io.Serializable;

public class GridClientNode {
    public static void main(String[] args) throws Throwable {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GridClientNodeConfig.class);
        IgniteConfiguration igniteConfiguration = context.getBean(IgniteConfiguration.class);

        QueryCursor<Cache.Entry<CacheAccountKey, Account>> queryCursor = null;
        try (Ignite ignite = Ignition.getOrStart(igniteConfiguration)) {
            logger(ignite).info("Started client node!");

            ContinuousQueryWithTransformer<CacheAccountKey, Account, TransformedEvent> query =
                    new ContinuousQueryWithTransformer<>();
            query.setRemoteTransformerFactory(FactoryBuilder.factoryOf(AccountChangedEventRmtTransformer.class));
            query.setLocalListener(GridClientNode::onUpdatedAccount);

            IgniteCache<CacheAccountKey, Account> cache = ignite.cache(Caches.ACCOUNT.getName());
            queryCursor = cache.query(query);

            for (int i = 0; i < 100; i++) {
                CacheAccountKey cacheAccountKey = new CacheAccountKey(i);
                Account account = new Account(new DomainAccountKey(i), "Person#" + i);
                System.out.println("Put account: " + account.getName());
                cache.put(cacheAccountKey, account);
            }

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

    private static void onUpdatedAccount(Iterable<? extends TransformedEvent> events) {
        for (TransformedEvent event : events) {
            System.out.println("Caught updated account: " + event.getAccountName());
        }
    }

    public static class AccountChangedEventRmtTransformer implements
            IgniteClosure<CacheEntryEvent<? extends CacheAccountKey, ? extends Account>, TransformedEvent>
    {
        private static final long serialVersionUID = 0;

        @Override
        public TransformedEvent apply(CacheEntryEvent<? extends CacheAccountKey, ? extends Account> cacheEntryEvent) {
            Account account = cacheEntryEvent.getValue();
            return new TransformedEvent(account.getName());
        }
    }

    public static class TransformedEvent implements Serializable {
        private static final long serialVersionUID = -8847917175123428480L;

        private final String accountName;

        public TransformedEvent(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountName() {
            return accountName;
        }
    }
}
