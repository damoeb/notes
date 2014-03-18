package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.metric.ServiceMetric;
import org.notes.search.interfaces.SearchManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/search")
public class SearchService {

    @Inject
    private SearchManager searchManager;

    @GET
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse search(
            @QueryParam("query") String query,
            @QueryParam("databaseId") Long databaseId,
            @QueryParam("start") Integer start,
            @QueryParam("rows") Integer rows,
            @QueryParam("context") Integer currentFolderId,
            @QueryParam("contextOnly") @DefaultValue("false") Boolean contextOnly

    ) throws Exception {
        try {
            return NotesResponse.ok(searchManager.query(query, start, rows, databaseId, currentFolderId, contextOnly));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
