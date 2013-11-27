package org.notes.common.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;
import org.notes.common.ForeignKey;
import org.notes.common.configuration.Configuration;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

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
        @NamedQuery(name = Document.QUERY_TRIGGER, query = "SELECT a FROM Document a where a.trigger in (:TRIGGER)")
})
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Document implements Serializable {

    public static final String QUERY_BY_ID = "Document.QUERY_BY_ID";
    public static final String QUERY_TRIGGER = "Document.QUERY_TRIGGER";

    public static final String FK_DOCUMENT_ID = "document_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Basic
    @Column(nullable = false, length = 256)
    private String title;

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

    @Column(insertable = false, updatable = false, name = ForeignKey.OWNER_ID)
    private Long ownerId;

    @Column(insertable = false, updatable = false, name = ForeignKey.FOLDER_ID)
    private Long folderId;

//    todo support doc properties
//    @ElementCollection
//	  @CollectionTable(name="attribute", joinColumns=@JoinColumn(name="document_id"))
//    private Map<String,String> attributes = new HashMap<String, String>(3);

    @Basic
    private boolean deleted;

    @Embedded
    private Reminder reminder;

    /**
     * % completed
     */
    @Basic
    private Integer progress;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date finished;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "event_trigger", nullable = true)
    @Index(name = "event_trigger_idx")
    private Trigger trigger;

//  -- Transient -------------------------------------------------------------------------------------------------------

    @Transient
    private Event event;

//  --------------------------------------------------------------------------------------------------------------------

    public Document() {
        // default
    }

    public Document(Long id, Kind kind) {
        this.id = id;
        this.kind = kind;
    }

    public Document(Long id, String title, String outline, Kind kind, Integer progress, Reminder reminder, Date modified) {
        this.id = id;
        this.title = title;
        this.outline = outline;
        this.kind = kind;
        this.progress = progress;
        this.modified = modified;
        this.reminder = reminder;
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

    protected void setKind(Kind kind) {
        this.kind = kind;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

}
