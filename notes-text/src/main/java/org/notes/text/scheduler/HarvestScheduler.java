package org.notes.text.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
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

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(0) // no concurrent access
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.HARVEST));
            List<Harvestable> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Harvestable harvestable : list) {

                    LOGGER.info("harvest " + harvestable.getId());

                    try {
                        // todo: do something
                        harvestable.getUrl();

                        // phantonjs
                        String pathToSnapshot = "/var/www/pdfjs/TestDocument.pdf";

                        FileReference ref = harvestManager.storeTemporary(pathToSnapshot);

                        harvestable.setSiteSnapshot(ref);

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
