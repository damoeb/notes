package org.notes.core;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

@Startup
@Singleton
@ApplicationScoped
public class StartUp {

    private static final Logger LOGGER = Logger.getLogger(StartUp.class);

    @ConfigurationProperty(value = Configuration.VERSION, defaultValue = "<UNKNOWN>")
    private String version = null;

    private boolean initialized = false;

    public StartUp() {
        //
    }

    public String getVersion() {
        return version;
    }

    @PostConstruct
    public void onInit() {

        synchronized (Startup.class) {
            if (!initialized) {
                initialized = false;

            }
        }

    }

    @PreDestroy
    public void preDestroy() throws Exception {
        LOGGER.info("Destroying...");
    }

}
