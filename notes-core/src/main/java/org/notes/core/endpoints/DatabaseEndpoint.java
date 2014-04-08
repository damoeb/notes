package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Database;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.DatabaseService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/database")
public class DatabaseEndpoint {

    @Inject
    private DatabaseService databaseService;

    @PUT
    @MethodCache
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            StandardDatabase database,
            @PathParam("id") long databaseId
    ) {
        try {
            Database result = databaseService.updateDatabase(databaseId, database);
            return NotesResponse.ok(result);
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long databaseId
    ) {
        try {
            Database database = databaseService.getDatabase(databaseId);
            return NotesResponse.ok(database);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/{id}/roots")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getRootFoldersInDatabase(
            @PathParam("id") long databaseId
    ) {
        try {

            return NotesResponse.ok(databaseService.getRootFolders(databaseId));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            @PathParam("id") long databaseId
    ) {
        try {
            return NotesResponse.ok(databaseService.deleteDatabase(databaseId));
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) {
        try {
            StandardDatabase database = databaseService.getDatabaseOfUser();
            database.setDefaultFolder(null);
            database.setActiveFolder(null);
            return NotesResponse.ok(database);
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }
}
