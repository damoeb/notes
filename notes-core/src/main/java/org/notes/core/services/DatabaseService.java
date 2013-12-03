package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.model.Database;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/database")
public class DatabaseService {

    @Inject
    private DatabaseManager databaseManager;

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDatabase(
            Database database
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.createDatabase(database));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @PUT
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Database database,
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.updateDatabase(databaseId, database));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getDatabase(databaseId));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
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

    @DELETE
    @MethodCache
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
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getDatabases());
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @Path("/{id}/document/open")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getOpenDocuments(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getOpenDocuments(databaseId));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @Path("/{id}/folder/open")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getOpenFolders(
            @PathParam("id") long databaseId
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.getOpenFolders(databaseId));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
