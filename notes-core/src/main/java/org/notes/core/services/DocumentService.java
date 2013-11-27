package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.Document;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/document")
public class DocumentService {

    @Inject
    private DocumentManager documentManager;

    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDocument(
            TextDocument document
    ) throws Exception {
        return NotesResponse.ok(getDocumentManager().createDocument(document));
    }

    @PUT
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDocument(
            TextDocument document
    ) throws Exception {
        Document result = getDocumentManager().updateDocument(document);
        return NotesResponse.ok(result);
    }

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}")
    public NotesResponse getDocument(
            @PathParam("id") long documentId
    ) {
        try {
            return NotesResponse.ok(getDocumentManager().getDocument(documentId));
        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDocument(
            TextDocument document
    ) throws Exception {
        return NotesResponse.ok(getDocumentManager().deleteDocument(document));
    }
}
