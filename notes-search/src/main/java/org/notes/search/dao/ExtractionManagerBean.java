package org.notes.search.dao;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.search.interfaces.ExtractionManager;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ExtractionManagerBean implements ExtractionManager {

    private static final Logger LOGGER = Logger.getLogger(ExtractionManagerBean.class);


    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void extract() {
        try {

            // todo get documents where Trigger.EXTRACT

        } catch (Throwable t) {
            // todo some probs
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
