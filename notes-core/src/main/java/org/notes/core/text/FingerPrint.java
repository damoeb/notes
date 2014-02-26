package org.notes.core.text;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "FingerPrint")
@Table(name = "FingerPrint")
@NamedQueries({
        @NamedQuery(name = FingerPrint.QUERY_ALL, query = "SELECT a FROM FingerPrint a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FingerPrint implements Serializable {

    public static final String QUERY_ALL = "FingerPrint.QUERY_ALL";

    @Id
    private long id;

//  --------------------------------------------------------------------------------------------------------------------

    public FingerPrint() {
        // default
    }
}
