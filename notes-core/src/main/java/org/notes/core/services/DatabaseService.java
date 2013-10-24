package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/database")
public class DatabaseService {

    @Inject
    private FolderManager folderManager;

    @Inject
    private UserManager userManager;


    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDatabase(
            Folder database
    ) throws Exception {
        return NotesResponse.ok(folderManager.createDatabase(database));
    }

    @PUT
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Folder database,
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.updateDatabase(folderId, database));
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabase(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.getDatabase(folderId));
    }

    @DELETE
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.deleteDatabase(folderId));
    }

    @GET
    @MethodCache
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDatabases(
    ) throws Exception {
        return NotesResponse.ok(folderManager.getDatabases());
    }

}
