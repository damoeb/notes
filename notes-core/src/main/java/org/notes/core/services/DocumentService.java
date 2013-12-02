package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.Folder;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/document")
public class DocumentService {

    @Inject
    private DocumentManager documentManager;

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDocument(
            TextDocument document
    ) throws Exception {

        Folder folder = null;
        if (document == null && document.getFolderId() != null) {
            folder = new Folder(document.getFolderId());
        }

        return NotesResponse.ok(documentManager.createDocument(document, folder));
    }

    @PUT
    @MethodCache
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDocument(
            TextDocument document,
            @PathParam("id") long documentId
    ) throws Exception {
        BasicDocument result = documentManager.updateDocument(document);
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
            return NotesResponse.ok(documentManager.getDocument(documentId));
        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDocument(
            @PathParam("id") long documentId
    ) throws Exception {
        return NotesResponse.ok(documentManager.deleteDocument(documentId));
    }
}
