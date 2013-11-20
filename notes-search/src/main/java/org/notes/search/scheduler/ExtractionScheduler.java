package org.notes.search.scheduler;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.Document;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    public void extract() {
        try {
            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Trigger.EXTRACT);
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

//    private Map<String, TextExtractor> typeToExtractor = new HashMap(10);
//
//    @PostConstruct
//    private void onInit() {
//        TextExtractor[] extractors = new TextExtractor[]{
//                new PdfTextExtractor()
//        };
//
//        for (TextExtractor extractor : extractors) {
//
//            if (extractor.getContentTypes() == null) {
//                LOGGER.error("content type is null");
//                continue;
//            }
//
//            for (String type : extractor.getContentTypes()) {
//                if (typeToExtractor.containsKey(type)) {
//                    LOGGER.error("duplicate content type " + type);
//                    continue;
//                }
//
//                typeToExtractor.put(type, extractor);
//            }
//
//        }
//    }
//
//    @Override
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NEVER)
//    @AccessTimeout(-1)
//    @Lock(LockType.READ)
//    public Future<String> extractAsync(FileReference reference) throws NotesException {
//        try {
//
//            TextExtractor extractor = getExtractor(reference);
//            if (extractor != null) {
//                extractor.extract(reference);
//            }
//
//            // todo implement
//            // return new AsyncResult<String>(reference.getContentType());
//            return null;
//
//        } catch (Throwable t) {
//            LOGGER.error(t.getMessage());
//            throw new NotesException(t.getMessage());
//        }
//    }
//
//    private TextExtractor getExtractor(FileReference reference) {
//        return typeToExtractor.get(reference.getContentType());
//    }
}
