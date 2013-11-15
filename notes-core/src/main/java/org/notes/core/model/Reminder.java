package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "Reminder")
@Table(name = "Reminder")
@NamedQueries({
        @NamedQuery(name = Reminder.QUERY_BY_ID, query = "SELECT a FROM Reminder a where a.id=:ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Reminder implements Serializable {

    public static final String QUERY_BY_ID = "Reminder.QUERY_BY_ID";
    public static final String FK_REMINDER_ID = "reminder_id";

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date referenceDate;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Repetition repetition;

    public Reminder() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }
}
