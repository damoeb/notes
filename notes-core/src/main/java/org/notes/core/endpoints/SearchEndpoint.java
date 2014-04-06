package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.QueryService;
import org.notes.core.services.SearchService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/search")
public class SearchEndpoint {

    @Inject
    private SearchService searchService;

    @Inject
    private QueryService queryService;

    @GET
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse search(
            @QueryParam("query") String query,
            @QueryParam("databaseId") Long databaseId,
            @QueryParam("start") Integer start,
            @QueryParam("rows") Integer rows,
            @QueryParam("context") Integer currentFolderId

    ) throws Exception {
        try {
            return NotesResponse.ok(searchService.query(query, start, rows, databaseId, currentFolderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse history() throws Exception {
        try {
            return NotesResponse.ok(queryService.history());
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}