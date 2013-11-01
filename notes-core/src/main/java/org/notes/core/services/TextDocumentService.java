package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.TextDocumentManager;
import org.notes.core.model.Document;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/document/text")
public class TextDocumentService {

    @Inject
    private TextDocumentManager documentManager;

    public TextDocumentManager getDocumentManager() {
        return documentManager;
    }

    @POST
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse createDocument(
            TextDocument document
    ) throws Exception {
        return NotesResponse.ok(getDocumentManager().createDocument(document));
    }

    @PUT
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse updateDocument(
            TextDocument document
    ) throws Exception {
        Document result = getDocumentManager().updateDocument(document);
        return NotesResponse.ok(result);
    }

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/{id}")
    public NotesResponse getDocument(
            @PathParam("id") long documentId
    ) {
        try {
            return NotesResponse.ok(getDocumentManager().getDocument(documentId));
        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }

    @DELETE
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse deleteDocument(
            TextDocument document
    ) throws Exception {
        return NotesResponse.ok(getDocumentManager().deleteDocument(document));
    }

    //    // -- ATTACHMENT -- ------------------------------------------------------------------------------------------------
//
//    @POST
//    @MethodCache
//    @Path(value = "/attachment/remove/{attachmentId}/{documentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public NotesResponse removeAttachment(
//            @PathParam("attachmentId") long attachmentId,
//            @PathParam("documentId") long documentId
//    ) throws Exception {
//        documentManager.removeAttachmentFromNote(attachmentId, documentId);
//        return NotesResponse.ok();
//    }
//
//    @POST
//    @MethodCache
//    @Path(value = "/attachment/rename/{attachmentId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public NotesResponse renameAttachment(
//            @PathParam("attachmentId") long attachmentId,
//            @QueryParam("name") String newName
//    ) throws Exception {
//        return NotesResponse.ok(documentManager.renameAttachment(attachmentId, newName));
//    }
//
//    // -- UPLOAD --
//
//    @POST
//    @Path("/attachment/add")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public NotesResponse upload(@Context HttpServletRequest request) {
//        try {
//
//            DiskFileItemFactory factory = new DiskFileItemFactory();
//
//            // Set factory constraints
//            factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
//            factory.setRepository(fileManager.getTempRepository());
//            //factory.setRepository(new File("/tmp"));
//
//            // Create a new file upload handler
//            ServletFileUpload upload = new ServletFileUpload(factory);
//
//            // Set overall request size constraint
//            //todo upload.setSizeMax(10000000);
//
//            List<FileItem> items = upload.parseRequest(request);
//            List<Attachment> files = new LinkedList<Attachment>();
//
//            Long noteId = _getNoteIdFieldVal(items);
//            Document note = documentManager.getByIdWithRefs(noteId);
//
//            for (FileItem item : items) {
//
//                if (!item.isFormField()) {
//
//                    RepositoryFile repositoryFile = fileManager.storeInRepository(item);
//                    files.add(documentManager.addAttachmentToNote(item.getName(), repositoryFile, note));
//                }
//            }
//
//            return NotesResponse.ok(files);
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//            return NotesResponse.error(t);
//        }
//    }
//
//
//    @GET
//    @Path("/attachment/{id}")
//    @Produces({"application/octet-stream", "text/plain"})
//
//    // todo http://javaevangelist.blogspot.ch/2012/01/jersey-tip-of-day-use-gzip-compression.html
//    //@org.jboss.resteasy.annotations.GZIP
//    public Response getAttachment(@PathParam("id") long attachmentId) {
//
//        try {
//
//            final Attachment data = documentManager.getAttachmentWithFile(attachmentId);
//
//            if (data == null) {
//                // force media type
//                return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(String.format("File '%s' appears to be empty.", attachmentId)).build();
//            }
//
//            CacheControl cc = new CacheControl();
//            cc.setNoTransform(true);
//            cc.setMustRevalidate(false);
//            cc.setNoCache(true);
//            cc.setMaxAge(3600);
//
//            final FileInputStream stream = new FileInputStream(data.getFileReference().getReference());
//
//            StreamingOutput entity = new StreamingOutput() {
//                @Override
//                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                    try {
//
//                        int len;
//                        byte[] buffer = new byte[1024];
//                        while ((len = stream.read(buffer)) > 0) {
//                            outputStream.write(buffer, 0, len);
//                        }
//
//                    } catch (Exception e) {
//                        throw new WebApplicationException(e);
//                    } finally {
//                        stream.close();
//                    }
//                }
//
//            };
//
//            String fileName = URLEncoder.encode(data.getName(), "UTF-8");
//            return Response
//                    .ok()
//                    .header("content-disposition", "attachment; filename*= UTF8''" + fileName)
//                    .type(data.getContentType())
//                    .entity(entity)
//                    .cacheControl(cc)
//                    .build();
//
//
//        } catch (Throwable t) {
//            // force media type
//            return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).entity(t.getMessage()).build();
//        }
//    }
//
//    private Long _getNoteIdFieldVal(List<FileItem> items) throws NotesException {
//        Long noteId = null;
//        String fieldName = "noteId";
//        for (FileItem item : items) {
//
//            if (item.isFormField()) {
//
//                String someFieldName = item.getFieldName();
//                if (StringUtils.equalsIgnoreCase(someFieldName, fieldName)) {
//
//                    if (NumberUtils.isNumber(item.getString())) {
//                        noteId = NumberUtils.createLong(item.getString());
//                    }
//                }
//            }
//        }
//
//        if (noteId == null) {
//            throw new NotesException("Required Field '" + fieldName + "' missing");
//        }
//
//        return noteId;
//    }
//
//
//    // -- LIST -- ------------------------------------------------------------------------------------------------------
//
//    @GET
//    @MethodCache
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "/list")
//    public NotesResponse getList(
//
//    ) throws Exception {
//        return getList(0, maxResults);
//    }
//
//    @GET
//    @MethodCache
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "/list/first:{firstResult}/max:{maxResults}")
//    public NotesResponse getList(
//            @PathParam("firstResult") int firstResult,
//            @PathParam("maxResults") int maxResults
//    ) throws Exception {
//        Map<String, Object> response = new HashMap<String, Object>(5);
//        response.put("firstResult", firstResult);
//        List<Document> list = documentManager.getList(firstResult, maxResults);
//        response.put("maxResults", list.size());
//        response.put("list", list);
//        return NotesResponse.ok(response);
//    }
}
