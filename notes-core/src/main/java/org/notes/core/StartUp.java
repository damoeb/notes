package org.notes.core;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.core.interfaces.*;
import org.notes.core.model.Account;
import org.notes.core.model.AccountType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@Singleton
@ApplicationScoped
public class StartUp {

    private static final Logger LOGGER = Logger.getLogger(StartUp.class);

    private boolean initialized = false;

    @Inject
    private DatabaseManager databaseManager;

    @Inject
    private FolderManager folderManager;

    @Inject
    private DocumentManager documentManager;

    @Inject
    private UserManager userManager;

    @Inject
    private AccountManager accountManager;

    public StartUp() {
        //
    }

    @PostConstruct
    public void onInit() {

        LOGGER.info("Notes Version " + Configuration.getStringValue(Configuration.VERSION, "UNKNOWN"));

        synchronized (Startup.class) {

            if (!initialized) {
                initialized = false;

                try {

                    Account a = new Account();
                    a.setType(AccountType.BASIC);
                    a.setQuota(1000l);
//                    a = accountManager.createAccount(a);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @PreDestroy
    public void preDestroy() throws Exception {
        LOGGER.info("Destroying...");
    }

}
