package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Folder;
import org.notes.common.services.FolderService;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.DocumentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@NotesInterceptors
@Path("/folder")
public class FolderEndpoint {

    @Inject
    private FolderService folderService;

    @Inject
    private DocumentService documentService;

    @POST
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createFolder(
            StandardFolder folder
    ) throws Exception {
        try {
            StandardFolder parent;
            if (folder.getParentId() != null) {
                parent = new StandardFolder(folder.getParentId());
            } else {
                parent = null;
            }
            StandardFolder result = (StandardFolder) folderService.createFolder(folder, parent, new StandardDatabase(folder.getDatabaseId()));
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
            StandardFolder folder,
            @PathParam("id") long folderId
    ) throws Exception {
        try {
            return NotesResponse.ok(folderService.updateFolder(folderId, folder));
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
            return NotesResponse.ok(folderService.getFolder(folderId));
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
            List<Folder> folders = folderService.getChildren(folderId);
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
            return NotesResponse.ok(documentService.getDocumentsInFolder(folderId));
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
            return NotesResponse.ok(folderService.deleteFolder(folderId));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
