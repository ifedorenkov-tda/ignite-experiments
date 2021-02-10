package org.example.config;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.sharedfs.TcpDiscoverySharedFsIpFinder;
import org.example.cache.CacheAccountKey;
import org.example.cache.Caches;
import org.example.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public abstract class GridNodeConfig {
    protected GridNodeConfig() {
    }

    protected IgniteConfiguration igniteConfiguration(@Autowired(required = false) List<LifecycleBean> lifecycleBeans)
            throws Throwable
    {
        IgniteConfiguration config = new IgniteConfiguration();
        config.setLocalHost("127.0.0.1");
        config.setCacheConfiguration(accountCache());
        config.setDiscoverySpi(discoverySpi());
        config.setCommunicationSpi(communicationSpi());
        config.setGridLogger(logger());
        if (lifecycleBeans != null && !lifecycleBeans.isEmpty()) {
            config.setLifecycleBeans(lifecycleBeans.toArray(new LifecycleBean[0]));
        }
        return config;
    }

    @Bean
    public IgniteLogger logger() throws Throwable {
        // Relative to META-INF :)
        return new Log4J2Logger("../log4j2.xml");
    }

    @Bean
    public DiscoverySpi discoverySpi() {
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalAddress("127.0.0.1");
        discoverySpi.setLocalPort(47500);
        discoverySpi.setLocalPortRange(5);
        discoverySpi.setIpFinder(new TcpDiscoverySharedFsIpFinder());
        return discoverySpi;
    }

    @Bean
    public CommunicationSpi<Message> communicationSpi() {
        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
        communicationSpi.setLocalAddress("127.0.0.1");
        communicationSpi.setLocalPort(47100);
        communicationSpi.setLocalPortRange(5);
        return communicationSpi;
    }

    @Bean
    public CacheConfiguration<CacheAccountKey, Account> accountCache() {
        CacheConfiguration<CacheAccountKey, Account> config = new CacheConfiguration<>();
        config.setName(Caches.ACCOUNT.getName());
        config.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        config.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        config.setCacheMode(CacheMode.PARTITIONED);
        config.setBackups(1);
        config.setSqlSchema("PUBLIC");

        /*config.setIndexedTypes(CacheAccountKey.class, Account.class);*/

        QueryEntity queryEntity = new QueryEntity(CacheAccountKey.class, Account.class);

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("affinityAccountKey.accountId", Long.class.getName());
        fields.put("name", String.class.getName());
        queryEntity.setFields(fields);

        Set<String> keyFields = new HashSet<>();
        keyFields.add("affinityAccountKey.accountId");
        queryEntity.setKeyFields(keyFields);

        queryEntity.setIndexes(Collections.singleton(new QueryIndex("affinityAccountKey.accountId")));
        queryEntity.setTableName("Account");

        Map<String, String> aliases = new HashMap<>();
        aliases.put("affinityAccountKey.accountId", "ACCOUNTID");
        aliases.put("name", "NAME");
        queryEntity.setAliases(aliases);

        config.setQueryEntities(Collections.singleton(queryEntity));

        return config;
    }
}
