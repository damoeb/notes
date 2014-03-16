package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.BookmarkDocument;
import org.notes.core.model.StandardFolder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/bookmark")
public class BookmarkService {

//  --------------------------------------------------------------------------------------------------------------------

    @Inject
    private DocumentManager documentManager;

    @POST
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse bookmark(BookmarkDocument bookmark) {
        try {

            StandardFolder folder = null;
            if (bookmark != null && bookmark.getFolderId() != null) {
                folder = new StandardFolder(bookmark.getFolderId());
            }

            return NotesResponse.ok(documentManager.bookmark(bookmark, folder));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @PUT
    @MethodCache
    @ServiceMetric
    @Path(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateBookmark(
            BookmarkDocument document,
            @PathParam("id") long documentId
    ) throws Exception {
        BasicDocument result = documentManager.updateBasicDocument(document);
        return NotesResponse.ok(result);
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}")
    public NotesResponse getDocument(
            @PathParam("id") long documentId
    ) {
        try {
            return NotesResponse.ok(documentManager.getDocument(documentId));
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

}
