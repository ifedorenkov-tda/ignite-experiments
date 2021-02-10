package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.events.CacheRebalancingEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.example.cache.Caches;
import org.example.config.GridNodeConfig;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CacheEventListenerService implements Service {
    private static final long serialVersionUID = -2195576259230481131L;

    public static final String SERVICE_NAME = "CacheEventListenerService";

    @IgniteInstanceResource
    private transient Ignite ignite;
    
    @LoggerResource
    private transient IgniteLogger logger;

    private transient ContinuousQuery<Long, String> cacheContinuousQuery;
    private transient QueryCursor<Cache.Entry<Long, String>> queryCursor;
    
    @Override
    public void cancel(ServiceContext ctx) {
        logger.info("Cancelling cache event listener service");

        if (queryCursor != null) {
            queryCursor.close();
        }
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        logger.info("Initializing cache event listener service");

        // Initialize a new instance of continuous query. This query keeps track of
        // accounts initialization and triggers processing of new accounts
        cacheContinuousQuery = new ContinuousQuery<>();
        cacheContinuousQuery.setLocal(true);

        // Just to be explicit
        cacheContinuousQuery.setAutoUnsubscribe(true);
        // Initialize the local listener
        cacheContinuousQuery.setLocalListener(events -> {
            System.out.println("[" + ignite.name() + "] Got event for keys: " +
                    StreamSupport.stream(events.spliterator(), false)
                        .map(CacheEntryEvent::getKey)
                        .collect(Collectors.toSet()));
        });
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        logger.info("Executing cache event listener service");

        Set<Integer> rebalancedPartitions = ConcurrentHashMap.newKeySet();
        IgnitePredicate<Event> partitionRebalancePredicate = event -> {
            CacheRebalancingEvent rebalancingEvent = (CacheRebalancingEvent) event;
            if (Caches.ACCOUNT.getName().equals(rebalancingEvent.cacheName())) {
                int partition = rebalancingEvent.partition();
                rebalancedPartitions.add(partition);
                System.out.println("Partition " +  partition + " rebalanced");
            }
            return true;
        };
        ignite.events().localListen(partitionRebalancePredicate, EventType.EVT_CACHE_REBALANCE_PART_LOADED);

        CountDownLatch rebalanceBarrier = new CountDownLatch(1);
        IgnitePredicate<Event> rebalanceStoppedPredicate = event -> {
            CacheRebalancingEvent rebalancingEvent = (CacheRebalancingEvent) event;
            if (Caches.ACCOUNT.getName().equals(rebalancingEvent.cacheName())) {
                rebalanceBarrier.countDown();
                return false;
            }
            return true;
        };
        ignite.events().localListen(rebalanceStoppedPredicate, EventType.EVT_CACHE_REBALANCE_STOPPED);

        logger.info("Waiting 10 seconds for the rebalance event");
        // wait at max 10 seconds for rebalance to complete before starting the query
        rebalanceBarrier.await(10, TimeUnit.SECONDS);
        ignite.events().stopLocalListen(partitionRebalancePredicate);
        ignite.events().stopLocalListen(rebalanceStoppedPredicate);

        logger.info("Rebalanced partitions: " + rebalancedPartitions);

        logger.info("Executing the query");

        // Initial query returns every account key that is currently in the cache
        if (rebalancedPartitions.isEmpty()) {
            ScanQuery<Long, String> initialQuery = new ScanQuery<>();
            initialQuery.setLocal(true);
            cacheContinuousQuery.setInitialQuery(initialQuery);
        } else {
            SqlQuery<Long, String> initialQuery = new SqlQuery<>(Long.class, "select key from ATOMICTESTCACHE");
            int[] partitionsArray = new int[rebalancedPartitions.size()];
            int i = 0;
            for (Integer p : rebalancedPartitions) {
                partitionsArray[i++] = p;
            }
            initialQuery.setPartitions(partitionsArray);
            cacheContinuousQuery.setInitialQuery(initialQuery);
        }

        queryCursor = ignite.cache(Caches.ACCOUNT.getName()).query(cacheContinuousQuery);
        queryCursor.forEach(cacheEntry -> {
            System.out.println("[" + ignite.name() + "] initial query returned key: " + cacheEntry.getKey());
        });
    }
}
