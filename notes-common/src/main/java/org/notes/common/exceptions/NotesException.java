package org.notes.common.exceptions;

public class NotesException extends Exception {

    private NotesStatus status;

    public NotesException(String message) {
        this(NotesStatus.ERROR, message);
    }

    public NotesException(String message, Throwable cause) {
        this(NotesStatus.ERROR, message, cause);
    }

    public NotesException(NotesStatus status, String message) {
        super(message);
        this.status = status;
    }

    public NotesException(NotesStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public NotesException(NotesStatus status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public NotesStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getStatus());
        stringBuilder.append(": ");
        stringBuilder.append(this.getMessage());
        return stringBuilder.toString();
    }
}
