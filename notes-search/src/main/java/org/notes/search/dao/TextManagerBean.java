package org.notes.search.dao;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.search.interfaces.TextExtractor;
import org.notes.search.interfaces.TextManager;
import org.notes.search.text.PdfTextExtractor;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class TextManagerBean implements TextManager {

    private static final Logger LOGGER = Logger.getLogger(TextManagerBean.class);

    private Map<String, TextExtractor> typeToExtractor = new HashMap(10);

    @PostConstruct
    private void onInit() {
        TextExtractor[] extractors = new TextExtractor[]{
                new PdfTextExtractor()
        };

        for (TextExtractor extractor : extractors) {

            if (extractor.getContentTypes() == null) {
                LOGGER.error("content type is null");
                continue;
            }

            for (String type : extractor.getContentTypes()) {
                if (typeToExtractor.containsKey(type)) {
                    LOGGER.error("duplicate content type " + type);
                    continue;
                }

                typeToExtractor.put(type, extractor);
            }

        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @AccessTimeout(-1)
    @Lock(LockType.READ)
    public Future<String> extractAsync(FileReference reference) throws NotesException {
        try {

            TextExtractor extractor = getExtractor(reference);
            if (extractor != null) {
                extractor.extract(reference);
            }

            return new AsyncResult<String>(reference.getContentType());

        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            throw new NotesException(t.getMessage());
        }
    }

    private TextExtractor getExtractor(FileReference reference) {
        return typeToExtractor.get(reference.getContentType());
    }
}
