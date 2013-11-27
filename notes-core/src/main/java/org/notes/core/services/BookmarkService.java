package org.notes.core.services;

import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.BookmarkDocument;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }

}
