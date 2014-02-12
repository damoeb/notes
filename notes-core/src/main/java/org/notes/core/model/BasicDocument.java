package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;
import org.notes.common.ForeignKey;
import org.notes.common.configuration.Configuration;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Document;
import org.notes.common.model.Kind;
import org.notes.common.model.Trigger;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The basic document
 */
@SuppressWarnings("serial")
@Entity(name = "BasicDocument")
@Table(name = "BasicDocument"
        //uniqueConstraints = @UniqueConstraint(columnNames = {"version", "foreignId"})
)
@NamedQueries({
        @NamedQuery(name = Document.QUERY_BY_ID, query = "SELECT a FROM BasicDocument a where a.id=:ID"),
        @NamedQuery(name = Document.QUERY_TRIGGER, query = "SELECT a FROM BasicDocument a where a.trigger in (:TRIGGER)"),
        // todo provide tags for next query
        @NamedQuery(name = BasicDocument.QUERY_IN_FOLDER, query = "SELECT new BasicDocument(a.id, a.uniqueHash, a.title, a.outline, a.kind, a.modified, a.star) FROM BasicDocument a where a.folderId=:ID AND a.deleted=false")
})
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicDocument implements Document {

    public static final String QUERY_IN_FOLDER = "BasicDocument.QUERY_IN_FOLDER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Basic
    @Column(nullable = false, length = 256)
    private String title;

    @Index(name = "uniqueHash")
    @Basic
    @Column(length = 256)
    private String uniqueHash;

    @Basic
    @Column(length = Configuration.Constants.OUTLINE_LENGTH)
    private String outline;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @JsonIgnore
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(insertable = false, updatable = false, name = ForeignKey.USER)
    private String owner;

    @Column(insertable = false, updatable = false, name = ForeignKey.FOLDER_ID)
    private Long folderId;

//    todo support doc properties
//    @ElementCollection
//	  @CollectionTable(name="attribute", joinColumns=@JoinColumn(name="document_id"))
//    private Map<String,String> attributes = new HashMap<String, String>(3);

    @JsonIgnore
    @Basic
    private boolean deleted;

    @Basic
    private boolean star;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "event_trigger", nullable = true)
    @Index(name = "event_trigger_idx")
    private Trigger trigger;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "document2tag")
    @Access(AccessType.FIELD)
    private Set<Tag> tags = new HashSet(100);

//  -- Transient -------------------------------------------------------------------------------------------------------

    @Transient
    private Event event;

//  --------------------------------------------------------------------------------------------------------------------

    public BasicDocument() {
        // default
    }

    public BasicDocument(Long id) {
        this.id = id;
    }

    public BasicDocument(Long id, Kind kind) {
        this.id = id;
        this.kind = kind;
    }

    public BasicDocument(Long id, String uniqueHash, String title, String outline, Kind kind, Date modified, boolean star) {
        this.id = id;
        this.uniqueHash = uniqueHash;
        this.title = title;
        this.outline = outline;
        this.kind = kind;
        this.modified = modified;
        this.star = star;
        this.tags = null;
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
//            throw new NotesException("BasicDocument is invalid", t);
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

    protected void setId(long id) {
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

    protected void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getOwner() {
        return owner;
    }

    protected void setOwner(String owner) {
        this.owner = owner;
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

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public Set<Tag> getTags() {
        // todo tags should be indexed
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public String getUniqueHash() {
        return uniqueHash;
    }

    public void setUniqueHash(String hash) {
        this.uniqueHash = hash;
    }

    public void validate() throws NotesException {
        if (StringUtils.isBlank(getTitle())) {
            throw new NotesException("Title is empty");
        }
    }
}
