package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.Database;
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

    @POST
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createFolder(
            Folder folder
    ) throws Exception {
        try {
            // todo fix does not work for folder without parent
            Folder parent;
            if (folder.getParentId() != null) {
                parent = new Folder(folder.getParentId());
            } else {
                parent = null;
            }
            Folder result = folderManager.createFolder(folder, parent, new Database(folder.getDatabaseId()));
            result.setDocuments(null);
            return NotesResponse.ok(result);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @PUT
    @MethodCache
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateFolder(
            Folder folder,
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(folderManager.updateFolder(folderId, folder));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    // todo not used
    @GET
    @MethodCache
    @ServiceMetric
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
    @ServiceMetric
    @Path("/{id}/children")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getChildren(
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            List<Folder> folders = folderManager.getChildren(folderId);
            return NotesResponse.ok(folders);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
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
    @ServiceMetric
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteFolder(
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(folderManager.deleteFolder(folderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
