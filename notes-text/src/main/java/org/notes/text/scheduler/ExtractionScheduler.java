package org.notes.text.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Extractable;
import org.notes.common.model.Document;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ExtractionScheduler {

    private static final Logger LOGGER = Logger.getLogger(ExtractionScheduler.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(0) // no concurrent access
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.EXTRACT_PDF));

            // todo get a list of extractables no casts
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    LOGGER.info("extract " + document.getId());

                    try {
                        if (document instanceof Extractable) {
                            ((Extractable) document).extract();
                        }
                        document.setTrigger(Trigger.INDEX);

                    } catch (NotesException e) {
                        // it failed
                        document.setTrigger(Trigger.OCR);
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
