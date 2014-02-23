package org.notes.search.scheduler;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Document;
import org.notes.common.interfaces.HarvestManager;
import org.notes.common.interfaces.Harvestable;
import org.notes.common.model.FileReference;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
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
    public void loop() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.HARVEST));
            List<Harvestable> list = query.getResultList();

            if (!list.isEmpty()) {

                int count = 0;

                for (Harvestable harvestable : list) {

                    LOGGER.info("harvest " + harvestable.getId());

//                    try {
//                        harvestable.setSiteSnapshot(harvest(harvestable.getUrl()));
//                    } catch (Throwable t) {
//                        LOGGER.error("harvest failed", t);
//                    }

                    org.jsoup.nodes.Document document = Jsoup.parse(new URL(harvestable.getUrl()), 4000);
                    document.setBaseUri(harvestable.getUrl()); // todo check

                    String text = document.text();

                    harvestable.setText(text);
                    harvestable.setTitle(document.title());
                    harvestable.setThumbnailUrl(getThumbnailUrl(document));

                    harvestable.setTrigger(Trigger.INDEX);

                    em.merge(harvestable);
                    em.flush();
                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);

        }
    }

    private String getThumbnailUrl(org.jsoup.nodes.Document document) {
        Element e = null;
        e = document.select("link[rel=apple-touch-icon][href][size=114x114]").first();
        if (e != null) {
            return e.absUrl("href");
        }
        e = document.select("link[rel=apple-touch-icon][href][size=72x72]").first();
        if (e != null) {
            return e.absUrl("href");
        }
        e = document.select("link[rel=apple-touch-icon][href]").first();
        if (e != null) {
            return e.absUrl("href");
        }
        e = document.select("link[rel=icon][href]").first();
        if (e != null) {
            return e.absUrl("href");
        }
        e = document.select("link[rel=shortcut icon][href]").first();
        if (e != null) {
            return e.absUrl("href");
        }

        return null;
    }

    private FileReference harvest(String url) throws IOException, InterruptedException, NotesException {
        final String phantomJs = Configuration.getStringValue(Configuration.CMD_PHANTOM_JS, null);
        final String rasterizeJs = Configuration.getStringValue(Configuration.CMD_RASTERIZE_JS, null);

        File tmpFile = new File(System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis() + "-" + Math.random() * 10000 + "-" + ".dat");
        ProcessBuilder builder = new ProcessBuilder()
                .inheritIO()
                .command(Arrays.asList(phantomJs, rasterizeJs, url, tmpFile.getAbsolutePath()));

        Process process = builder.start();
        process.waitFor();

        return harvestManager.storeTemporary(tmpFile.getAbsolutePath());

    }
}
