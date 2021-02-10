package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.compute.ComputeJobContext;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.JobContextResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.example.cache.CacheAccountKey;
import org.example.cache.Caches;
import org.example.domain.Account;
import org.example.domain.AccountDao;
import org.example.domain.DomainAccountKey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestServiceImpl extends AbstractService implements TestService {
    private static final long serialVersionUID = 7515594166386332757L;

    @IgniteInstanceResource
    private transient Ignite ignite;

    @LoggerResource(categoryClass = TestServiceImpl.class)
    private transient IgniteLogger logger;

    private transient ExecutorService executorService;

    @Override
    public void cancel(ServiceContext ctx) {
        logger.info("Canceling service.");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted", e);
        }
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        logger.info("Initializing service.");
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        logger.info("Executing service.");


        logger.info("Submitting the cache operation.");

        IgniteFuture<Void> promise = ignite.compute(ignite.cluster().forServers())
                .withExecutor("MyExecutor")
                .runAsync(new IgniteRunnableAdapter());
        promise.listen(completedPromise -> {
            try {
                completedPromise.get();
                logger.info("OK!");
            } catch (IgniteException e) {
                logger.error("An error has happened", e);
            }
        });
    }

    @Override
    public void doSomething() {
        logger.info("doSomething()");
    }

    public static class IgniteRunnableAdapter implements IgniteRunnable {
        private static final long serialVersionUID = 4927774586999234101L;

        @JobContextResource
        private transient ComputeJobContext context;

        @IgniteInstanceResource
        private transient Ignite ignite;

        @SpringResource(resourceClass = AccountDao.class)
        private transient AccountDao accountDao;

        @Override
        public void run() {
            if (ignite.name().equals("SecondNode")) {
                System.exit(-1);
            } else {
                System.out.println("OK");
            }
        }
    }
}
