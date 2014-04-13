package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Folder;
import org.notes.common.services.FolderService;
import org.notes.core.domain.Operation;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.interceptors.Bouncer;
import org.notes.core.metric.PerformanceLogger;
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
    @PerformanceLogger
    @Bouncer(op = Operation.NEW_FOLDER)
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createFolder(
            StandardFolder folder) {

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

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @PUT
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateFolder(
            StandardFolder folder,
            @PathParam("id") long folderId) {

        try {
            return NotesResponse.ok(folderService.updateFolder(folderId, folder));

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/{id}/children")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getChildren(
            @PathParam("id") long folderId) {

        try {
            List<Folder> folders = folderService.getChildren(folderId);
            return NotesResponse.ok(folders);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/{id}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getDocumentsInFolder(
            @PathParam("id") long folderId) {
        try {
            return NotesResponse.ok(documentService.getDocumentsInFolder(folderId));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @PerformanceLogger
    @Bouncer
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteFolder(
            @PathParam("id") long folderId
    ) {
        try {
            folderService.deleteFolder(folderId);
            return NotesResponse.ok();
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }
}
