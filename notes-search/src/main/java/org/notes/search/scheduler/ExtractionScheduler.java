package org.notes.search.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.Document;
import org.notes.common.model.Trigger;
import org.notes.search.interfaces.TextExtractor;
import org.notes.search.text.PdfTextExtractor;

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
public class ExtractionScheduler {

    private static final Logger LOGGER = Logger.getLogger(ExtractionScheduler.class);

    @Inject
    private
    @PdfTextExtractor
    TextExtractor textExtractor;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.EXTRACT));
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    LOGGER.info("extract " + document.getId());

                    document.extractFullText();
                    document.setTrigger(Trigger.INDEX);
                    em.merge(document);
                    em.flush();
                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }
}
