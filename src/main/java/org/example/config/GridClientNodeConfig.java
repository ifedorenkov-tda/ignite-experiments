package org.example.config;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GridClientNodeConfig extends GridNodeConfig {
    @Bean
    public IgniteConfiguration igniteConfiguration(@Autowired(required = false) List<LifecycleBean> lifecycleBeans)
            throws Throwable
    {
        IgniteConfiguration config = super.igniteConfiguration(lifecycleBeans);
        config.setClientMode(true);
        return config;
    }
}
