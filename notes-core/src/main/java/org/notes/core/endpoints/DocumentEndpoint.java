package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.BasicDocument;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.TextDocument;
import org.notes.core.endpoints.request.MoveDocumentParams;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.DocumentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/document")
public class DocumentEndpoint {

    @Inject
    private DocumentService documentService;

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

        return NotesResponse.ok(documentService.createDocument(document, folder));
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
        BasicDocument result = documentService.updateTextDocument(document);
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
        BasicDocument result = documentService.updateBasicDocument(document);
        return NotesResponse.ok(result);
    }

    @POST
    @MethodCache
    @ServiceMetric
    @Path(value = "/move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public NotesResponse moveDocument(
            MoveDocumentParams payload
    ) throws Exception {

        documentService.moveTo(payload.getDocumentIds(), payload.getToFolderId());
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
            return NotesResponse.ok(documentService.getDocument(documentId));
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
        return NotesResponse.ok(documentService.deleteDocument(documentId));
    }
}
