package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

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
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDatabase(
            Database database
    ) throws Exception {
        try {
            Database result = databaseManager.updateDatabase(database);
            result.setFolders(null);
            return NotesResponse.ok(result);
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
            Database database = databaseManager.getDatabase(databaseId);
            Map<Long, Folder> folders = new HashMap(100);
            Comparator<Folder> sortedByName = new Comparator<Folder>() {
                @Override
                public int compare(Folder f1, Folder f2) {
                    return f2.getName().compareTo(f1.getName());
                }
            };
            SortedSet tree = new TreeSet(sortedByName);
            for (Folder f : database.getFolders()) {
                f.setDocuments(null);
                folders.put(f.getId(), f);
                if (f.getLevel() == 0) {
                    tree.add(f);
                }
            }
            for (Folder f : database.getFolders()) {
                if (f.getLevel() != 0) {
                    Folder parent = folders.get(f.getParentId());
                    if (parent.getChildren() == null) {
                        parent.setChildren(new TreeSet(sortedByName));
                    }
                    parent.getChildren().add(f);
                    parent.setDocumentCount(parent.getDocumentCount() + f.getDocumentCount());
                }
            }

            database.setFolders(tree);
            return NotesResponse.ok(database);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDatabase(
            Database database
    ) throws Exception {
        try {
            return NotesResponse.ok(databaseManager.deleteDatabase(database));
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
            List<Database> databases = databaseManager.getDatabases();
            for (Database database : databases) {
                database.setFolders(null);
            }
            return NotesResponse.ok(databases);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
