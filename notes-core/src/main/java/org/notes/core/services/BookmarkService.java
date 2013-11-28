package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.BookmarkDocument;

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
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse bookmark(BookmarkDocument bookmark) {
        try {

            return NotesResponse.ok(documentManager.bookmark(bookmark));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
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
            return NotesResponse.error(t);
        }
    }

}
