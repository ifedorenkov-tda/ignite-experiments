package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.example.config.GridClientNodeConfig;
import org.example.service.AnotherTestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GridClientSecondNode {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GridClientNodeConfig.class);
        IgniteConfiguration igniteConfiguration = context.getBean(IgniteConfiguration.class);
        try (Ignite ignite = Ignition.getOrStart(igniteConfiguration)) {
            logger(ignite).info("Started client node!");

            AnotherTestService testService =
                    ignite.services().serviceProxy(AnotherTestService.SERVICE_NAME, AnotherTestService.class, true);
            testService.doSomething();

            logger(ignite).info("Done!");
        }
    }

    private static IgniteLogger logger(Ignite ignite) {
        return ignite.log().getLogger(GridClusterNode.class);
    }
}
