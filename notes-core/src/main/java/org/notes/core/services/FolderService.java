package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@NotesInterceptors
@Path("/folder")
public class FolderService {

    @Inject
    private FolderManager folderManager;

    @Inject
    private UserManager userManager;

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createFolder(
            Folder database
    ) throws Exception {
        return NotesResponse.ok(folderManager.createFolder(database));
    }

    @PUT
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateFolder(
            Folder database,
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.updateFolder(folderId, database));
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getFolder(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.getFolder(folderId));
    }

    @DELETE
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteFolder(
            @PathParam("id") long folderId
    ) throws Exception {
        return NotesResponse.ok(folderManager.deleteFolder(folderId));
    }


}
