package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.search.interfaces.SearchManager;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/query")
public class SearchService {

    @Inject
    private SearchManager searchManager;

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse search(
            @QueryParam("query") String query,
            @QueryParam("databaseId") Long databaseId,
            @QueryParam("folderId") Long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(searchManager.query(query, databaseId, folderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
