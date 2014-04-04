package org.notes.search.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Document;
import org.notes.common.domain.Extractable;
import org.notes.common.domain.Trigger;
import org.notes.common.exceptions.NotesException;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

@Deprecated
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ExtractionScheduler {

    private static final Logger LOGGER = Logger.getLogger(ExtractionScheduler.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false, info = "Extracts text from PDFs")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(-1) // no concurrent access
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.EXTRACT_PDF));

            List<Extractable> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Extractable extractable : list) {

                    LOGGER.info("extract " + extractable.getId());

                    try {
                        extractable.extract();
                        extractable.setTrigger(Trigger.INDEX);

                    } catch (NotesException e) {
                        // it failed
                        extractable.setTrigger(Trigger.EXTRACT_FAILED);
                    }

                    em.merge(extractable);
                    em.flush();
                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }
}
