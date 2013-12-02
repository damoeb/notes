package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.BookmarkDocument;
import org.notes.core.model.Folder;

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

            Folder folder = null;
            if (bookmark == null && bookmark.getFolderId() != null) {
                folder = new Folder(bookmark.getFolderId());
            }

            return NotesResponse.ok(documentManager.bookmark(bookmark, folder));

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
