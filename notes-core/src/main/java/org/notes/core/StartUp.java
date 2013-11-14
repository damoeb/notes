package org.notes.core;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.core.interfaces.*;
import org.notes.core.model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

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
    private TextDocumentManager documentManager;

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


                try {

                    Account a = new Account();
                    a.setName("Basic");
                    a.setQuota(1000l);
                    a = accountManager.createAccount(a);


                    User u = new User();
                    u.setUsername("TestUser");
                    userManager.createUser(u, a);

                    Database d = new Database();
                    d.setName("work");
                    d = databaseManager.createDatabase(d);

                    Folder f0 = new Folder();
                    f0.setName("Projekte");
                    f0.setDatabaseId(d.getId());
                    folderManager.createFolder(f0);

                    Folder f1 = new Folder();
                    f1.setName("Ideen");
                    f1.setDatabaseId(d.getId());
                    f1 = folderManager.createFolder(f1);

                    Folder f2 = new Folder();
                    f2.setName("2014");
                    f2.setParentId(f1.getId());
                    f2.setDatabaseId(d.getId());
                    f2 = folderManager.createFolder(f2);

                    TextDocument td1 = new TextDocument();
                    td1.setTitle("Wende im Fall Taboga");
                    td1.setText("Der SV Grödig hat den Vertrag mit Dominique Taboga aufgelöst. Dies gab der Salzburger Verein im Rahmen einer Pressekonferenz am Donnerstag bekannt. Manager Christian Haas");
                    td1.setProgress(24);
                    td1.setFolderId(f2.getId());

                    td1 = documentManager.createDocument(td1);

                    TextDocument td2 = new TextDocument();
                    td2.setTitle("Kosmische Atomschleuder");
                    td2.setText("Rätsel rund um Schwarze Löcher gelöst: Die Schwerkraftmonster schleudern große Mengen verschiedener Atome mit rund 200.000 Kilometern pro Sekunde ins All hinaus");
                    td2.setFolderId(f2.getId());

                    td2 = documentManager.createDocument(td2);

                    Folder f3 = new Folder();
                    f3.setName("Herbst");
                    f3.setParentId(f2.getId());
                    f3.setDatabaseId(d.getId());
                    f3 = folderManager.createFolder(f3);

                    TextDocument td3 = new TextDocument();
                    td3.setTitle("Es ist wieder Zeit, pseudowissenschaftliche \"Leistungen\" zu nominieren");
                    td3.setText("Der gleichnamige Preis wird von der Gesellschaft für kritisches Denken (GkD), der Wiener Regionalgruppe der internationalen Skeptikervereinigung GWUP (Gesellschaft zur Wissenschaftlichen Untersuchung von Parawissenschaften), bereits zum dritten Mal vergeben. Er soll zum einen auf unwissenschaftliches Vorgehen an sich aufmerksam machen, zum anderen aber auch auf die oft blühenden Geschäfte, die auf dieser Grundlage gedeihen.");
                    td3.setFolderId(f3.getId());

                    Reminder reminder = new Reminder();
                    reminder.setReferenceDate(new Date());
                    reminder.setRepetition(Repetition.WEEKLY);
                    td3.setReminder(reminder);

                    td3 = documentManager.createDocument(td3);


                    //documentManager.deleteDocument(td1.getId());

                    //documentManager.getDocument(td2.getId());

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

    }

    @PreDestroy
    public void preDestroy() throws Exception {
        LOGGER.info("Destroying...");
    }

}
