package org.example.service;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

public class AnotherTestServiceImpl implements AnotherTestService, Service {
    private static final long serialVersionUID = -5144304555080082320L;

    @LoggerResource
    private transient IgniteLogger logger;

    private String aField;

    public AnotherTestServiceImpl(String aField) {
        this.aField = aField;
    }

    @Override
    public void cancel(ServiceContext ctx) {
        logger.info("Canceling service.");
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        logger.info("Initializing service.");
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        logger.info("Executing service. aField = " + aField);
    }

    @Override
    public void changeState() {
        logger.info("Changing state...!");
    }

    @Override
    public void doSomething() {
        logger.info("I am doing something...!");
        aField = "Oooops!";
    }
}
