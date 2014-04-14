package org.notes.core.domain;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.domain.Extractable;
import org.notes.common.domain.FileReference;
import org.notes.common.domain.FullText;
import org.notes.common.domain.Kind;
import org.notes.common.exceptions.NotesException;
import org.notes.common.utils.TextUtils;
import org.notes.search.ExtractionResult;
import org.notes.search.PdfTextExtractor;
import org.notes.search.interfaces.TextExtractor;

import javax.naming.InitialContext;
import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity(name = "PdfDocument")
@Table(name = "PdfDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PdfDocument extends BasicDocument implements Extractable {

    private static final Logger LOGGER = Logger.getLogger(PdfDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false, targetEntity = StandardFileReference.class)
    @JoinColumn(name = ForeignKey.FILE_REFERENCE_ID)
    private FileReference fileReference;

    @Column(insertable = false, updatable = false, name = ForeignKey.FILE_REFERENCE_ID)
    private Long fileReferenceId;

//  --------------------------------------------------------------------------------------------------------------------

    @Basic
    private int numberOfPages;

    public PdfDocument() {
        // default
        setKind(Kind.PDF);
    }

    public FileReference getFileReference() {
        return fileReference;
    }

    public void setFileReference(FileReference fileReference) {
        this.fileReference = fileReference;
    }

    public Long getFileReferenceId() {
        return fileReferenceId;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @Override
    public void onPersist() {
        super.onPersist();

        FileReference reference = getFileReference();

        int len = reference.getFullTexts() == null ? 0 : Math.min(2, reference.getFullTexts().size());
        String[] more = new String[len];
        int index = 0;
        // todo: fulltexts should be ordered
        for (FullText fullText : reference.getFullTexts()) {
            if (index >= more.length) {
                break;
            }
            more[index++] = fullText.getText();
        }

        setOutline(TextUtils.toOutline(reference.getSize() + " bytes", more));
    }

    @JsonIgnore
    @Override
    public void extract() throws NotesException {
        try {

            FileReference reference = getFileReference();

            if (reference.getFullTexts() == null || reference.getFullTexts().isEmpty()) {
                LOGGER.trace("extract from pdf " + getId());

                long time = System.currentTimeMillis();

                InitialContext ic = new InitialContext();

                TextExtractor extractor = (TextExtractor) ic.lookup("java:comp/env/" + PdfTextExtractor.BEAN_NAME);
                ExtractionResult result = extractor.extract(reference);
                Map<Integer, String> extracted = result.getFullTexts();

                Set<FullText> fullTexts = new HashSet<>(extracted.size());
                for (Integer page : extracted.keySet()) {
                    String text = extracted.get(page);
                    fullTexts.add(new StandardFullText(page, text));
                }

                ((StandardFileReference) reference).setFullTexts(fullTexts);
                setNumberOfPages(result.getNumberOfPages());

                long delta = System.currentTimeMillis() - time;

                LOGGER.info(String.format("pdf %s extracted in %s", getId(), delta / 1000d));
            }

        } catch (Throwable e) {
            throw new NotesException("Cannot extract text of " + getId(), e);
        }
    }

    @Override
    @JsonIgnore
    public Collection<FullText> getTexts() {
        return fileReference.getFullTexts();
    }
}
