package org.notes.search.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Document;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ExtractionScheduler {

    private static final Logger LOGGER = Logger.getLogger(ExtractionScheduler.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(value = 3, unit = TimeUnit.SECONDS)
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.EXTRACT));
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    LOGGER.info("extract " + document.getId());

                    try {
                        document.extractFullText();
                        document.setTrigger(Trigger.INDEX);

                    } catch (NotesException e) {
                        document.setTrigger(Trigger.EXTRACT_FAILED);
                    }

                    em.merge(document);
                    em.flush();
                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }
}
