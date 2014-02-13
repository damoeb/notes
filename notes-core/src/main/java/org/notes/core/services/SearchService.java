package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.metric.ServiceMetric;
import org.notes.search.interfaces.SearchManager;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
            @QueryParam("databaseId") long databaseId,
            @QueryParam("start") int start,
            @QueryParam("rows") int rows

    ) throws Exception {
        try {
            return NotesResponse.ok(searchManager.query(databaseId, query, start, rows));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
