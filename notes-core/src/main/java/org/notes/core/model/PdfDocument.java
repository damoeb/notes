package org.notes.core.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.model.Document;
import org.notes.common.model.FileReference;

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

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = FileReference.FK_FILE_REFERENCE_ID)
    private FileReference fileReference;

    @JsonIgnore
    @Lob
    @Column(name = "full_text")
    private String fullText;

    public PdfDocument() {
        // default
    }

    public FileReference getFileReference() {
        return fileReference;
    }

    public void setFileReference(FileReference fileReference) {
        this.fileReference = fileReference;
    }

    @Override
    public void extractFullText() {
        fullText = "Some extracted fulltext";
        // todo check if file is already extracted
        LOGGER.info("Extract from PDF");
    }
}
