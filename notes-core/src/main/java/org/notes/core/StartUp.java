package org.notes.core;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.services.FolderService;
import org.notes.core.domain.Account;
import org.notes.core.domain.AccountType;
import org.notes.core.services.AccountService;
import org.notes.core.services.DatabaseService;
import org.notes.core.services.DocumentService;
import org.notes.core.services.UserService;

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
    private DatabaseService databaseService;

    @Inject
    private FolderService folderService;

    @Inject
    private DocumentService documentService;

    @Inject
    private UserService userService;

    @Inject
    private AccountService accountService;

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
                    a = accountService.createAccount(a);

                } catch (Exception e) {
                    LOGGER.error("cannot initialize db. Reason: " + e.getMessage());
                }

            }
        }

    }

    @PreDestroy
    public void preDestroy() throws Exception {
        LOGGER.info("Destroying...");
    }

}
