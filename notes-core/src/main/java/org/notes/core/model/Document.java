package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jsoup.Jsoup;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
@Entity(name = "Document")
@Table(name = "Document"
        //uniqueConstraints = @UniqueConstraint(columnNames = {"version", "foreignId"})
)
@NamedQueries({
        //@NamedQuery(name = Document.QUERY_BY_ID, query = "SELECT a FROM Document a where a.id=:ID"),
        @NamedQuery(name = Document.QUERY_BY_ID, query = "SELECT a FROM Document a where a.id=:ID"),
        @NamedQuery(name = Document.QUERY_REMOVE, query = "DELETE FROM Document a where a.id=:ID"),
        @NamedQuery(name = Document.QUERY_ALL, query = "SELECT a FROM Document a")
})
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Document implements Serializable {

    public static final String QUERY_BY_ID = "Document.QUERY_BY_ID";
    public static final String QUERY_ALL = "Document.QUERY_ALL";
    public static final String QUERY_REMOVE = "Document.QUERY_REMOVE";
    public static final String FK_NOTE_ID = "note_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Basic
    @Column(nullable = false, length = 256)
    // todo length
    private String title;

    @Basic
    @Column(length = 1024)
    private String url;

    @Lob
    private String text;

    @Basic
    private String preview;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Basic
    private long size;

    @Column(nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(insertable = false, updatable = false, name = User.FK_OWNER_ID)
    private Long ownerId;

    @Column(insertable = false, updatable = false, name = Folder.FK_FOLDER_ID)
    private Long folderId;

    @Basic
    private boolean hasAttachments;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Document.FK_NOTE_ID)
    // too join without table
    private List<Attachment> attachments = new LinkedList<Attachment>();

    /*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webarchive_id")
    private FileReference webArchive;

    @Column(name = "webarchive_id", updatable = false, insertable = false)
    private Long webArchiveId;

*/

    // -- Tags -- ------------------------------------------------------------------------------------------------------

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "article_tag_mapping",
//            joinColumns = {@JoinColumn(name = "articleId")},
//            inverseJoinColumns = {@JoinColumn(name = "tagId")}
//    )
//    private Set<Tag> tags = new HashSet<Tag>();

    public Document() {
        // default
    }

    @PrePersist
    @PreUpdate
    public void onPersist() {
        setPreview(_getPreview(this));
        Date now = new Date();
        if (getCreated() == null) {
            setCreated(now);
        }
        setModified(now);

        long totalSize = _getBytes(getTitle()) + _getBytes(getText()) + _getBytes(getUrl());
        for (Attachment reference : getAttachments()) {
            totalSize += reference.getSize();
        }
        setSize(totalSize);

        setKind(_getKind(this));

        //StringBuilder builder = new StringBuilder(2000);
//        builder.append(StringUtils.defaultIfBlank(description, ""));
//        if (content != null) {
//            if (StringUtils.isBlank(content.getText())) {
//                if (!StringUtils.isBlank(content.getHtml())) {
//                    // todo render
//                    builder.append(content.getHtml());
//                }
//            } else {
//                builder.append(content.getText().trim());
//            }
//        }
//        text = builder.toString();
    }

    private Kind _getKind(Document note) {

//        TEXT: default
        Kind kindOfDoc = Kind.TEXT;

        boolean hasText = StringUtils.isNotBlank(Jsoup.parse(note.getText()).text());
        boolean hasUrl = StringUtils.isNotBlank(note.getUrl());

//        BOOKMARK: text=0, url=1
        if(!hasText && hasUrl) {
            kindOfDoc = Kind.BOOKMARK;
        }

//        PDF: attachments.size()==1 && attachments[0]==PDF
//        DOC: attachments.size()==1 && attachments[0]==DOC

//        ARCHIVE: attachments.size()>1
        if(note.getAttachments().size()>1) {
            kindOfDoc = Kind.ARCHIVE;
        }

        return kindOfDoc;
    }

    private long _getBytes(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return value.getBytes().length;
    }

    private String _getPreview(Document note) {
        String plain = Jsoup.parse(note.getText()).text().replaceAll("\t\n ", " ");
        int limit = 80;
        return plain.length() > limit ? plain.substring(0, limit) + "..." : plain;
    }

    /**
     * @throws org.notes.common.exceptions.NotesException
     *          if an assignment is illegal
     */
//    @SuppressWarnings({"ConstantConditions"})
//    public void validateFields() throws NotesException {
//
//        final List<String> missingFields = new LinkedList<String>();
//
//        try {
//            // -- Check fields
//            for (Field field : getClass().getDeclaredFields()) {
//                String fieldName = field.getName();
//                Column column = field.getAnnotation(Column.class);
//                try {
//                    if (column != null) {
//                        Object value = getFieldValue(field);
//                        // -- Null-check for inconsistencies
//                        boolean isEmpty = value == null || (value instanceof String && StringUtils.isBlank((String) value));
//                        if (!column.nullable() && isEmpty)
//                            if (!(missingFields.contains(fieldName))) missingFields.add(fieldName);
//
//                        // -- Length
//                        if (value instanceof String) {
//                            String _value = StringUtils.trim((String) value);
//                            int maxLength = column.length() / 2;
//                            if ((!field.isAnnotationPresent(Lob.class)) && _value.length() > maxLength) {
//                                throw new NotesException(NotesStatus.PARAMETER_TOO_LONG,
//                                        fieldName + " is too long. Maximal length is " + maxLength);
//                            }
//                        }
//                    }
//
//                } catch (NoSuchMethodException e) {
//                    // ignore
//                }
//            }
//            if (!missingFields.isEmpty()) {
//                throw new NotesException(NotesStatus.PARAMETER_MISSING,
//                        "The following field(s) must be set: " + StringUtils.join(missingFields, ", "));
//            }
//
//        } catch (NotesException e) {
//            throw e;
//        } catch (Throwable t) {
//            throw new NotesException("Document is invalid", t);
//        }
//    }
//
//    private Object getFieldValue(Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        field.setAccessible(true);
//        return field.get(this);
//    }
//
//    private boolean isEmptyField(Field field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//        Object value = getFieldValue(field);
//
//        return (value == null
//                || (value instanceof String && StringUtils.isBlank((String) value)));
//    }

    // -- GETTER/SETTER -- ---------------------------------------------------------------------------------------------
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public long getSize() {
        return size;
    }

    private void setSize(long size) {
        this.size = size;
    }

    public Kind getKind() {
        return kind;
    }

    private void setKind(Kind kind) {
        this.kind = kind;
    }


}
