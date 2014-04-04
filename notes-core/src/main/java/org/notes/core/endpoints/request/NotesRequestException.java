package org.notes.core.endpoints.request;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Deprecated
public class NotesRequestException extends WebApplicationException {

    private Response.Status status;
    private String message;

    public NotesRequestException(Response.Status status, String message) {
        super(status);
        this.status = status;
        this.message = message;
    }

    public NotesRequestException(String message, Throwable throwable) {
        this(Response.Status.INTERNAL_SERVER_ERROR, throwable.getMessage());
    }

    public NotesRequestException(Response.Status status, Throwable throwable) {
        this(status, throwable.getMessage());
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

//    public NotesRequestException(Response.Status status, String message) {
//        super(message);
//        this.status = status;
//    }
//
//    public NotesRequestException(NotesStatus status, String message, Throwable cause) {
//        super(message, cause);
//        this.status = status;
//    }
//
//    public NotesRequestException(NotesStatus status, Throwable cause) {
//        super(cause);
//        this.status = status;
//    }
//
//
//    public NotesRequestException(String message, Throwable cause) {
//        super(message, cause);
//    }
//
//    public NotesStatus getStatus() {
//        return status;
//    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getStatus());
        stringBuilder.append(": ");
        stringBuilder.append(this.getMessage());
        return stringBuilder.toString();
    }
}
