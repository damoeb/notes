package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;

@Entity(name = "FileDocument")
@Table(name = "FileDocument")
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FileDocument extends Document {

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = FileReference.FK_FILE_REFERENCE_ID)
    private FileReference fileReference;

    public FileDocument() {
        // default
    }

    public FileReference getFileReference() {
        return fileReference;
    }

    public void setFileReference(FileReference fileReference) {
        this.fileReference = fileReference;
    }
}
