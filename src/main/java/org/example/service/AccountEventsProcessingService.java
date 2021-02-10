package org.example.service;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

public class AccountEventsProcessingService implements Service {
    public static final String SERVICE_NAME = "AccountEventsProcessingService";

    @LoggerResource(categoryClass = AccountEventsProcessingService.class)
    private transient IgniteLogger logger;

    private final AccountEventsProcessingProperties properties;

    public AccountEventsProcessingService(IgniteLogger logger, AccountEventsProcessingProperties properties) {
        this.properties = properties;
        logger.info("Ctor: " + properties);
    }

    @Override
    public void cancel(ServiceContext ctx) {
        logger.info("Cancel: " + properties);
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        logger.info("Init: " + properties);
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        logger.info("Execute: " + properties);
    }
}
