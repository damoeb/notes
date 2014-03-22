package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.StandardFolder;
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
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/text")
    public NotesResponse createTextDocument(
            TextDocument document
    ) throws Exception {

        StandardFolder folder = null;
        if (document != null && document.getFolderId() != null) {
            folder = new StandardFolder(document.getFolderId());
        }

        return NotesResponse.ok(documentManager.createDocument(document, folder));
    }

    @PUT
    @MethodCache
    @ServiceMetric
    @Path(value = "/text/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateTextDocument(
            TextDocument document,
            @PathParam("id") long documentId
    ) throws Exception {
        BasicDocument result = documentManager.updateTextDocument(document);
        return NotesResponse.ok(result);
    }

    @PUT
    @MethodCache
    @ServiceMetric
    @Path(value = "/basic/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateBasicDocument(
            BasicDocument document,
            @PathParam("id") long documentId
    ) throws Exception {
        BasicDocument result = documentManager.updateBasicDocument(document);
        return NotesResponse.ok(result);
    }

    @POST
    @MethodCache
    @ServiceMetric
    @Path(value = "/move/{documentId}/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse moveDocument(
            @PathParam("documentId") Long documentId,
            @PathParam("folderId") Long folderId
    ) throws Exception {
        documentManager.moveTo(documentId, folderId);
        return NotesResponse.ok();
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getAnyDocument(
            @PathParam("id") long documentId
    ) {
        try {
            return NotesResponse.ok(documentManager.getDocument(documentId));
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @ServiceMetric
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDocument(
            @PathParam("id") long documentId
    ) throws Exception {
        return NotesResponse.ok(documentManager.deleteDocument(documentId));
    }
}
