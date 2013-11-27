package org.notes.core.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Extractable;
import org.notes.common.model.Document;
import org.notes.common.model.FileReference;
import org.notes.common.model.FullText;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;
import org.notes.text.ExtractionResult;
import org.notes.text.PdfTextExtractor;
import org.notes.text.interfaces.TextExtractor;

import javax.naming.InitialContext;
import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity(name = "PdfDocument")
@Table(name = "PdfDocument"
//    todo uniqueConstraints = @UniqueConstraint(columnNames = {
//            FileReference.FK_FILE_REFERENCE_ID,
//            Folder.FK_FOLDER_ID
//    })
)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PdfDocument extends Document implements Extractable {

    private static final Logger LOGGER = Logger.getLogger(PdfDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
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

    public void setFileReferenceId(Long fileReferenceId) {
        this.fileReferenceId = fileReferenceId;
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
                Set<FullText> fullTexts = result.getFullTexts();
                if (fullTexts.isEmpty()) {
                    // todo check if working
                    throw new NotesException("FullText not extractable");
                }

                reference.setFullTexts(fullTexts);
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
    public Collection<FullText> getFullTexts() {
        return fileReference.getFullTexts();
    }
}
