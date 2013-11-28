package org.notes.text.scheduler;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.interfaces.Document;
import org.notes.common.interfaces.HarvestManager;
import org.notes.common.interfaces.Harvestable;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class HarvestScheduler {

    private static final Logger LOGGER = Logger.getLogger(HarvestScheduler.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private HarvestManager harvestManager;

    @Schedule(second = "*/3", minute = "*", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(0) // no concurrent access
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.HARVEST));
            List<Harvestable> list = query.getResultList();

            if (!list.isEmpty()) {

                String phantomJs = Configuration.getStringValue(Configuration.CMD_PHANTOM_JS, null);
                String rasterizeJs = Configuration.getStringValue(Configuration.CMD_RASTERIZE_JS, null);

                File tmpFile = new File(System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis() + "-" + Math.random() * 10000 + ".pdf");

                for (Harvestable harvestable : list) {

                    LOGGER.info("harvest " + harvestable.getId());

                    try {
                        org.jsoup.nodes.Document document = Jsoup.parse(new URL(harvestable.getUrl()), 4000);

//                        ProcessBuilder builder = new ProcessBuilder()
//                                .inheritIO()
//                                .command(Arrays.asList(phantomJs, rasterizeJs, harvestable.getUrl(), tmpFile.getAbsolutePath()));
//
//                        Process process = builder.start();
//                        process.waitFor();
//
//                        FileReference ref = harvestManager.storeTemporary(tmpFile.getAbsolutePath());
//                        harvestable.setSiteSnapshot(ref);
//
//                        harvestable.setText(document.text());

                        harvestable.setOutline(harvestable.getUrl());
                        harvestable.setTitle(document.title());

                        harvestable.setTrigger(Trigger.INDEX);

                    } catch (Throwable e) {
                        e.printStackTrace();
                        // it failed
                        harvestable.setTrigger(Trigger.HARVEST_FAILED);
                    }

                    em.merge(harvestable);
                    em.flush();
                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }
}
