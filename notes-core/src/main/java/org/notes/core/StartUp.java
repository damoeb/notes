package org.notes.core;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.core.interfaces.*;

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

    @ConfigurationProperty(value = Configuration.VERSION, defaultValue = "<UNKNOWN>")
    private String version = null;

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

    public String getVersion() {
        return version;
    }

    @PostConstruct
    public void onInit() {

        synchronized (Startup.class) {
            if (!initialized) {
                initialized = false;


//                try {
//
//                    Account a = new Account();
//                    a.setName("Basic");
//                    a.setQuota(1000l);
//                    a = accountManager.createAccount(a);
//
//
//                    User u = new User();
//                    u.setUsername("TestUser");
//                    userManager.createUser(u, a);
//
//                    Database d = new Database();
//                    d.setName("work");
//                    d = databaseManager.createDatabase(d);
//
//                    Folder f1 = new Folder();
//                    f1.setName("Ideen");
//                    f1.setDatabaseId(d.getId());
//
//                    f1 = folderManager.createFolder(f1);
//
//                    Folder f2 = new Folder();
//                    f2.setName("2014");
//                    f2.setParent(f1);
//                    f2.setDatabaseId(d.getId());
//
//                    f2 = folderManager.createFolder(f2);
//
//                    TextDocument td1 = new TextDocument();
//                    td1.setTitle("Text Document");
//                    td1.setText("blabla");
//                    td1.setDescription("First document");
//                    td1.setFolderId(f2.getId());
//
//                    td1 = documentManager.createTextDocument(td1);
//
//                    TextDocument td2 = new TextDocument();
//                    td2.setTitle("Text Document2");
//                    td2.setText("blabla");
//                    td2.setDescription("Second document");
//                    td2.setFolderId(f2.getId());
//
//                    td2 = documentManager.createTextDocument(td2);
//
//                    documentManager.deleteDocument(td1.getId());
//
//                    //documentManager.getDocument(td2.getId());
//
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }

            }
        }

    }

    @PreDestroy
    public void preDestroy() throws Exception {
        LOGGER.info("Destroying...");
    }

}
