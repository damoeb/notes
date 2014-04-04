package org.notes.recommend.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Document;
import org.notes.common.domain.Trigger;
import org.notes.recommend.service.TextEssence;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

//@LocalBean
@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class EssenceCalculatorScheduler {

    private static final Logger LOGGER = Logger.getLogger(EssenceCalculatorScheduler.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private TextEssence textEssence;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(-1) // no concurrent access
//    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void index() {

        try {

            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.ESSENCE));
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    LOGGER.info("essence " + document.getId());

//                    document.setEssence(textEssence.getBestKeywords(50, document.getTexts()));

                    document.setTrigger(null);
                    em.merge(document);

                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }


}
