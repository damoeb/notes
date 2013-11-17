package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.configuration.Configuration;
import org.notes.common.model.Kind;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;
import org.notes.search.interfaces.Indexable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The basic document
 */
@SuppressWarnings("serial")
@Entity(name = "Document")
@Table(name = "Document"
        //uniqueConstraints = @UniqueConstraint(columnNames = {"version", "foreignId"})
)
@NamedQueries({
        @NamedQuery(name = Document.QUERY_BY_ID, query = "SELECT a FROM Document a where a.id=:ID"),
        @NamedQuery(name = Document.QUERY_WITH_REMINDER, query = "SELECT a FROM Document a LEFT JOIN FETCH a.reminder where a.id=:ID"),
        @NamedQuery(name = Document.DELETE_DOCUMENT, query = "UPDATE Document a set a.deleted = true where a.id=:ID and a.ownerId=:OWNER"),
        @NamedQuery(name = Document.QUERY_ALL, query = "SELECT a FROM Document a")
})
@Inheritance(strategy = InheritanceType.JOINED)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Document implements Serializable, Indexable {

    public static final String QUERY_BY_ID = "Document.QUERY_BY_ID";
    public static final String QUERY_WITH_REMINDER = "Document.QUERY_WITH_REMINDER";
    public static final String QUERY_ALL = "Document.QUERY_ALL";
    public static final String DELETE_DOCUMENT = "Document.DELETE_DOCUMENT";

    public static final String FK_NOTE_ID = "note_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Basic
    @Column(nullable = false, length = 256)
    private String title;

    @JsonIgnore
    @Lob
    @Column(name = "full_text")
    private String fulltext;

    @Basic
    @Column(length = Configuration.Constants.OUTLINE_LENGTH)
    private String outline;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

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
    private Integer progress;

    @Basic
    private boolean deleted;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = Reminder.FK_REMINDER_ID)
    private Reminder reminder;

    @Column(insertable = false, updatable = false, name = Reminder.FK_REMINDER_ID)
    private Long reminderId;

    public Document() {
        // default
    }

    public Document(Long id, String title, String outline, Kind kind, Integer progress, Long reminderId, Date modified) {
        this.id = id;
        this.title = title;
        this.outline = outline;
        this.kind = kind;
        this.progress = progress;
        this.reminderId = reminderId;
        this.modified = modified;
    }

    @PrePersist
    @PreUpdate
    public void onPersist() {
        Date now = new Date();
        if (getCreated() == null) {
            setCreated(now);
        }
        setModified(now);
    }
//
//    private long _getBytes(String value) {
//        if (StringUtils.isBlank(value)) {
//            return 0;
//        }
//        return value.getBytes().length;
//    }

//    todo validate fields
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }
}
