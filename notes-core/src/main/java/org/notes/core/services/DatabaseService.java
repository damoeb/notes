package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/database")
public class DatabaseService {

    @Inject
    private DatabaseManager databaseManager;

    @Inject
    private UserManager userManager;


    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDatabase(
            Database database
    ) throws Exception {
        return NotesResponse.ok(databaseManager.createDatabase(database));
    }

    @PUT
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Database database,
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(databaseManager.updateDatabase(folderId, database));
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(databaseManager.getDatabase(folderId));
    }

    @DELETE
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(databaseManager.deleteDatabase(folderId));
    }

    @GET
    @MethodCache
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) throws Exception {
        return NotesResponse.ok(databaseManager.getDatabases());
    }

}
