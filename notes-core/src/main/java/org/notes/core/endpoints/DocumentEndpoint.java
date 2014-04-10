package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.BasicDocument;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.TextDocument;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.endpoints.request.MoveDocumentParams;
import org.notes.core.interceptors.Bouncer;
import org.notes.core.metric.PerformanceLogger;
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
    @PerformanceLogger
    @Bouncer
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/text")
    public NotesResponse createTextDocument(
            TextDocument document) {

        try {
            StandardFolder folder = null;
            if (document != null && document.getFolderId() != null) {
                folder = new StandardFolder(document.getFolderId());
            }

            return NotesResponse.ok(documentService.createDocument(document, folder));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @PUT
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path(value = "/text/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateTextDocument(
            TextDocument document,
            @PathParam("id") long documentId) {

        try {
            BasicDocument result = documentService.updateTextDocument(document);
            return NotesResponse.ok(result);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @PUT
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path(value = "/basic/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateBasicDocument(
            BasicDocument document,
            @PathParam("id") long documentId) {

        try {
            BasicDocument result = documentService.updateBasicDocument(document);
            return NotesResponse.ok(result);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @POST
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path(value = "/move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public NotesResponse moveDocument(
            MoveDocumentParams payload) {
        try {
            documentService.moveTo(payload.getDocumentIds(), payload.getToFolderId());
            return NotesResponse.ok();

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getAnyDocument(
            @PathParam("id") long documentId) {

        try {
            return NotesResponse.ok(documentService.getDocument(documentId));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDocument(
            @PathParam("id") long documentId
    ) {
        try {
            return NotesResponse.ok(documentService.deleteDocument(documentId));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }
}
