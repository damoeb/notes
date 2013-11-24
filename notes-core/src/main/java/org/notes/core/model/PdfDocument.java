package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Document;
import org.notes.common.model.FileReference;
import org.notes.common.utils.TextUtils;
import org.notes.search.interfaces.TextExtractor;
import org.notes.search.text.ExtractionResult;
import org.notes.search.text.PdfTextExtractor;

import javax.naming.InitialContext;
import javax.persistence.*;

@Entity(name = "PdfDocument")
@Table(name = "PdfDocument"
//    todo uniqueConstraints = @UniqueConstraint(columnNames = {
//            FileReference.FK_FILE_REFERENCE_ID,
//            Folder.FK_FOLDER_ID
//    })
)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PdfDocument extends Document {

    private static final Logger LOGGER = Logger.getLogger(PdfDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = FileReference.FK_FILE_REFERENCE_ID)
    private FileReference fileReference;

    @Column(insertable = false, updatable = false, name = FileReference.FK_FILE_REFERENCE_ID)
    private Long fileReferenceId;

//  --------------------------------------------------------------------------------------------------------------------

    @Basic
    private int numberOfPages;

    public PdfDocument() {
        // default
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
        setOutline(TextUtils.toOutline(reference.getSize() + " bytes", reference.getFullText()));
    }

    @Override
    public void extractFullText() throws NotesException {
        try {

            LOGGER.info("extract pdf " + getId());

            FileReference reference = getFileReference();

            if (StringUtils.isBlank(reference.getFullText())) {
                InitialContext ic = new InitialContext();

                TextExtractor extractor = (TextExtractor) ic.lookup("java:comp/env/" + PdfTextExtractor.BEAN_NAME);
                ExtractionResult result = extractor.extract(reference);
                reference.setFullText(StringUtils.join(result.getFullTexts(), " "));
                setNumberOfPages(result.getNumberOfPages());
            }

        } catch (Throwable e) {
            throw new NotesException("Cannot extract text of " + getId(), e);
        }
    }
}
