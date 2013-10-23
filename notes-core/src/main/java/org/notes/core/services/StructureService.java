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
@Path("/structure")
public class StructureService {

    @Inject
    private FolderManager folderManager;

    @Inject
    private UserManager userManager;



    @POST
    @MethodCache
    @Path("/database/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder createDatabase(
            @QueryParam("name") String name
    ) throws Exception {
        return folderManager.createDatabase(name);
    }

    @GET
    @MethodCache
    @Path("/database/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Folder> getDatabases(
    ) throws Exception {
        return folderManager.getDatabases();
    }

    // -- Folder -------------------------------------------------------------------------------------------------------

    @POST
    @MethodCache
    @Path("/folder/rename/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder renameFolder(
            @PathParam("folderId") Long folderId,
            @QueryParam("name") String name
    ) throws Exception {
        return folderManager.renameFolder(folderId, name);
    }

    @POST
    @MethodCache
    @Path("/folder/remove/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removeFolder(
            @PathParam("folderId") Long folderId
    ) throws Exception {
        folderManager.removeFolder(folderId);
    }

    @POST
    @MethodCache
    @Path("/folder/add/{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder createFolder(
            @PathParam("parentId") Long parentId,
            @QueryParam("name") String name
    ) throws Exception {
        return folderManager.createFolder(parentId, name);
    }

    @POST
    @MethodCache
    @Path("/folder/move/{folderId}/{newParentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder moveFolder(
            @PathParam("folderId") Long folderId,
            @PathParam("newParentId") Long newParentId
    ) throws Exception {
        return folderManager.moveFolder(folderId, newParentId);
    }

    @POST
    @MethodCache
    @Path("/note/move/{noteId}/{newParentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder moveNote(
            @PathParam("noteId") Long noteId,
            @PathParam("newParentId") Long newParentId
    ) throws Exception {
        return folderManager.moveNote(noteId, newParentId);
    }

    @GET
    @MethodCache
    @Path("/folder/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Folder getFolder(
            @PathParam("folderId") Long folderId
    ) throws Exception {
        return folderManager.getById(folderId);
    }

    @GET
    @MethodCache
    @Path("/folder/{folderId}/children")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Folder> children(
            @PathParam("folderId") Long folderId
    ) throws Exception {
        return folderManager.getChildren(folderId);
    }

}
