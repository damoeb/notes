package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.Database;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/database")
public class DatabaseService {

    @Inject
    private DatabaseManager databaseManager;

    @PUT
    @MethodCache
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Database database,
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            Database result = databaseManager.updateDatabase(databaseId, database);
            result.setOpenFolders(null);
            return NotesResponse.ok(result);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            Database database = databaseManager.getDatabase(databaseId);
            database.setOpenFolders(null);
            return NotesResponse.ok(database);

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/{id}/roots")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getRootFoldersInDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getFolders(databaseId));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/{id}/open")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getOpenFoldersInDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getOpenFolders(databaseId));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @DELETE
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.deleteDatabase(databaseId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getDatabasesOfCurrentUser());
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }
}
