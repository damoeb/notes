package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.SearchResponse;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.interceptors.Bouncer;
import org.notes.core.metric.PerformanceLogger;
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

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse search(
            @QueryParam("query") String query,
            @QueryParam("databaseId") Long databaseId,
            @QueryParam("start") Integer start,
            @QueryParam("rows") Integer rows,
            @QueryParam("context") Integer currentFolderId
    ) {

        try {

            SearchResponse response = searchService.find(query, start, rows, databaseId, currentFolderId);

            searchService.asyncQueryLogging(query);

            return NotesResponse.ok(response);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse history() {
        try {
            return NotesResponse.ok(searchService.getLastQueries());

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/suggest")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse suggest(@QueryParam("query") String query) {
        try {
            return NotesResponse.ok(searchService.getQuerySuggestions(query));
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

}
