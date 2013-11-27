package org.notes.core.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Extractable;
import org.notes.common.model.FileReference;
import org.notes.common.model.FullText;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "BookmarkDocument")
@Table(name = "BookmarkDocument"
//    todo uniqueConstraints = @UniqueConstraint(columnNames = {
//            FileReference.FK_FILE_REFERENCE_ID,
//            Folder.FK_FOLDER_ID
//    })
)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BookmarkDocument extends TextDocument implements Extractable {

    private static final Logger LOGGER = Logger.getLogger(BookmarkDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ForeignKey.FILE_REFERENCE_ID)
    private FileReference fileReference;

    @Column(insertable = false, updatable = false, name = ForeignKey.FILE_REFERENCE_ID)
    private Long fileReferenceId;

//  --------------------------------------------------------------------------------------------------------------------

    @Basic
    @Column(nullable = false, length = 1024)
    private String url;


    @Lob
    private String text;

    public BookmarkDocument() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String fullText) {
        this.text = fullText;
    }

    @JsonIgnore
    @Override
    public void extract() throws NotesException {
//        try {
//
//            LOGGER.info("extract bookmark " + getId());
//
//            FileReference reference = getFileReference();
//
//            if (reference.getFullTexts() == null || reference.getFullTexts().isEmpty()) {
//
//                // todo implement
//
//            }
//
//        } catch (Throwable e) {
//            throw new NotesException("Cannot extract text of " + getId(), e);
//        }
    }

    @JsonIgnore
    @Override
    public Collection<FullText> getFullTexts() {
        return Arrays.asList(new FullText(1, text));
    }
}
