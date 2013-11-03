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
            Folder folder
    ) throws Exception {
        try {
            Folder result = folderManager.createFolder(folder);
            result.setDocuments(null);
            result.setChildren(null);
            return NotesResponse.ok(result);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @PUT
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateFolder(
            Folder folder
    ) throws Exception {
        try {
            Folder result = folderManager.updateFolder(folder);
            result.setDocuments(null);
            result.setChildren(null);
            return NotesResponse.ok(result);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getFolder(
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(folderManager.getFolder(folderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @Path("/{id}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDocumentsInFolder(
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(folderManager.getDocuments(folderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteFolder(
            Folder folder
    ) throws Exception {
        try {
            return NotesResponse.ok(folderManager.deleteFolder(folder));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }


}
