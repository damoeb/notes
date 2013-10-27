package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Database;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Database database
    ) throws Exception {
        Database result = databaseManager.updateDatabase(database);
        result.setFolders(null);
        return NotesResponse.ok(result);
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long databaseId
    ) throws Exception {
        return NotesResponse.ok(databaseManager.getDatabase(databaseId));
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            Database database
    ) throws Exception {
        return NotesResponse.ok(databaseManager.deleteDatabase(database));
    }

    @GET
    @MethodCache
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) throws Exception {
        List<Database> databases = databaseManager.getDatabases();
        for (Database database : databases) {
            database.setFolders(null);
        }
        return NotesResponse.ok(databases);
    }

}
