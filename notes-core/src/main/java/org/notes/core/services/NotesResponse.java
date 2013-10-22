package org.notes.core.services;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.exceptions.NotesException;
import org.notes.common.exceptions.NotesStatus;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NotesResponse {

    private Object result;

    private int statusCode;
    private String status;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date timestamp = new Date();

    private String errorMessage;

    private NotesResponse() {
        this.result = null;
        this.errorMessage = null;
        this.setStatus(NotesStatus.OK);
    }

    private NotesResponse(Object result) {
        this.result = result;
        this.errorMessage = null;
        this.setStatus(NotesStatus.OK);
    }

    private NotesResponse(Throwable e) {
        this.result = null;
        this.errorMessage = e.getMessage();
        if (e instanceof NotesException) {
            this.setStatus(((NotesException) e).getStatus());
        } else {
            this.setStatus(NotesStatus.ERROR);
        }
    }

    private void setStatus(NotesStatus status) {
        this.statusCode = status.getStatusCode();
        this.status = status.toString();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Object getResult() {
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static NotesResponse ok() {
        return new NotesResponse();
    }

    public static NotesResponse ok(Object result) {
        return new NotesResponse(result);
    }

    public static NotesResponse error(Throwable t) {
        return new NotesResponse(t);
    }
}
