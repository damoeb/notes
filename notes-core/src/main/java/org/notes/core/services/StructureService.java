package org.notes.core.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;
import org.notes.core.request.NotesRequestException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@NotesInterceptors
@Path("/structure")
public class StructureService {

    @Inject
    private FolderManager folderManager;

    @Inject
    private UserManager userManager;

    // jstree

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public String doGet(
            @QueryParam("operation") @DefaultValue("create_node") String operationValue, @QueryParam("id") Long folderId
    ) throws Exception {

        Operation op = Operation.byString(operationValue);

        if(op==null) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid operation '%s'", operationValue));
        }

        if(folderId==null) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid id '%s'", folderId));
        }

        Collection<Folder> children = folderManager.getChildren(folderId);

        JSONArray result = new JSONArray();

        for(Folder folder : children) {

            JSONObject jFolder = new JSONObject();
            JSONObject jAttr = new JSONObject();
            jAttr.put("id", folder.getId());
            jFolder.put("attr", jAttr);
            jFolder.put("data", folder.getName());

            jAttr.put("rel", "folder");
            jFolder.put("state", "closed");

            result.put(jFolder);
        }

//        id	381
//        operation	get_children

//        [
//        {
//            "attr":{
//            "id":"372",
//                    "rel":"folder"
//        },
//            "data":"New node",
//                "state":"closed"
//        },
//        {
//            "attr":{
//            "id":"381",
//                    "rel":"folder"
//        },
//            "data":"New node",
//                "state":"closed"
//        }
//        ]
        return result.toString();
    }


    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public String doPost(
            @FormParam("operation") String operationValue, @FormParam("id") Long folderId, @FormParam("title") String folderName
    ) throws Exception {

        Operation op = Operation.byString(operationValue);

        if(op==null) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid operation '%s'", operationValue));
        }
        if(folderId==null) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid id '%s'", folderId));
        }

        JSONObject response = new JSONObject();
        response.put("status", 1);

        switch (op) {
            case create_node:
                Folder folder = folderManager.createFolder(folderId, userManager.getUserId(), folderName);
                response.put("id", folder.getId());
                break;
            case remove_node:
                folderManager.removeFolder(folderId);
                break;
            case rename_node:
                folderManager.renameFolder(folderId, folderName);
                break;
            default:
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid operation '%s'", operationValue));
        }

//        -- create
//        id	372
//        operation	create_node
//        position	6
//        title	wef
//        type	default/folder
//
//        -- remove
//        id	375
//        operation	remove_node

        return response.toString();
    }

}
