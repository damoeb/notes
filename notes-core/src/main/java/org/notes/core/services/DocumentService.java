package org.notes.core.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.dao.RepositoryFile;
import org.notes.core.interfaces.FileManager;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.Attachment;
import org.notes.core.model.Document;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@NotesInterceptors
@Path("/document")
public class DocumentService {

    @Inject
    private DocumentManager documentManager;
    @Inject
    private FileManager fileManager;

    private int maxResults;
    private FastDateFormat dateFormat;

    @PostConstruct
    public void onInit() {
        maxResults = Configuration.getIntValue("query.max.results", 1000);
        dateFormat = FastDateFormat.getInstance(Configuration.getStringValue(Configuration.REST_TIME_PATTERN, "yyyy-MM-dd HH:mm"));
    }

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}")
    public NotesResponse getById(
            @PathParam("id") long noteId
    ) {
        try {
            return NotesResponse.ok(documentManager.getByIdWithRefs(noteId));
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse addNote(
            Document note
    ) throws Exception {
        Document result = documentManager.addDocument(note);
        result.setAttachments(null);
        return NotesResponse.ok(result);
    }

    @POST
    @MethodCache
    @Path(value = "/remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse removeNote(
            @PathParam("id") long noteId
    ) throws Exception {
        documentManager.removeNote(noteId);
        return NotesResponse.ok();
    }

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}")
    public NotesResponse updateNote(
            Document note,
            @PathParam("id") long noteId
    ) throws Exception {
        Document result = documentManager.updateNote(noteId, note);
        result.setAttachments(null);
        return NotesResponse.ok(result);
    }

    // todo check url, wenn url hinzugefügt, buttons anzeigen für harvest,...


    // -- ATTACHMENT -- ------------------------------------------------------------------------------------------------

    @POST
    @MethodCache
    @Path(value = "/attachment/remove/{attachmentId}/{noteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse removeAttachment(
            @PathParam("attachmentId") long attachmentId,
            @PathParam("noteId") long noteId
    ) throws Exception {
        documentManager.removeAttachmentFromNote(attachmentId, noteId);
        return NotesResponse.ok();
    }

    @POST
    @MethodCache
    @Path(value = "/attachment/rename/{attachmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse renameAttachment(
            @PathParam("attachmentId") long attachmentId,
            @QueryParam("name") String newName
    ) throws Exception {
        return NotesResponse.ok(documentManager.renameAttachment(attachmentId, newName));
    }

    // -- UPLOAD --

    @POST
    @Path("/attachment/add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse upload(@Context HttpServletRequest request) {
        try {

            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
            factory.setRepository(fileManager.getTempRepository());
            //factory.setRepository(new File("/tmp"));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            //todo upload.setSizeMax(10000000);

            List<FileItem> items = upload.parseRequest(request);
            List<Attachment> files = new LinkedList<Attachment>();

            Long noteId = _getNoteIdFieldVal(items);
            Document note = documentManager.getByIdWithRefs(noteId);

            for(FileItem item:items){

                if (!item.isFormField()) {

                    RepositoryFile repositoryFile = fileManager.storeInRepository(item);
                    files.add(documentManager.addAttachmentToNote(item.getName(), repositoryFile, note));
                }
            }

            return NotesResponse.ok(files);

        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }


    @GET
    @Path("/attachment/{id}")
    @Produces({"application/octet-stream", "text/plain"})

    // todo http://javaevangelist.blogspot.ch/2012/01/jersey-tip-of-day-use-gzip-compression.html
    //@org.jboss.resteasy.annotations.GZIP
    public Response getAttachment(@PathParam("id") long attachmentId) {

        try {

            final Attachment data  = documentManager.getAttachmentWithFile(attachmentId);

            if(data == null) {
                // force media type
                return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(String.format("File '%s' appears to be empty.", attachmentId)).build();
            }

            CacheControl cc = new CacheControl();
            cc.setNoTransform(true);
            cc.setMustRevalidate(false);
            cc.setNoCache(true);
            cc.setMaxAge(3600);

            final FileInputStream stream = new FileInputStream(data.getFileReference().getReference());

            StreamingOutput entity = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    try {

                        int len;
                        byte[] buffer = new byte[1024];
                        while((len = stream.read(buffer))>0) {
                            outputStream.write(buffer, 0, len);
                        }

                    } catch (Exception e) {
                        throw new WebApplicationException(e);
                    } finally {
                        stream.close();
                    }
                }

            };

            String fileName = URLEncoder.encode(data.getName(), "UTF-8");
            return Response
                    .ok()
                    .header("content-disposition", "attachment; filename*= UTF8''"+ fileName)
                    .type(data.getContentType())
                    .entity(entity)
                    .cacheControl(cc)
                    .build();


        } catch (Throwable t) {
            // force media type
            return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(t.getMessage()).build();
        }
    }

    private Long _getNoteIdFieldVal(List<FileItem> items) throws NotesException {
        Long noteId = null;
        String fieldName = "noteId";
        for(FileItem item:items){

            if (item.isFormField()) {

                String someFieldName = item.getFieldName();
                if(StringUtils.equalsIgnoreCase(someFieldName, fieldName)) {

                    if(NumberUtils.isNumber(item.getString())) {
                        noteId = NumberUtils.createLong(item.getString());
                    }
                }
            }
        }

        if(noteId==null) {
            throw new NotesException("Required Field '"+fieldName+"' missing");
        }

        return noteId;
    }


    // -- LIST -- ------------------------------------------------------------------------------------------------------

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/list")
    public NotesResponse getList(

    ) throws Exception {
        return getList(0, maxResults);
    }

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/list/first:{firstResult}/max:{maxResults}")
    public NotesResponse getList(
            @PathParam("firstResult") int firstResult,
            @PathParam("maxResults") int maxResults
    ) throws Exception {
        Map<String, Object> response = new HashMap<String, Object>(5);
        response.put("firstResult", firstResult);
        List<Document> list = documentManager.getList(firstResult, maxResults);
        response.put("maxResults", list.size());
        response.put("list", list);
        return NotesResponse.ok(response);
    }
}
