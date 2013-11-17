package org.notes.core.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.TextDocumentManager;
import org.notes.core.model.Document;
import org.notes.core.model.FileDocument;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.List;

@NotesInterceptors
@Path("/document")
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/upload")
    public NotesResponse upload(@Context HttpServletRequest request) {
        try {

            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
            factory.setRepository(new File("/tmp"));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            //todo upload.setSizeMax(10000000);

            List<FileItem> items = upload.parseRequest(request);

            //String tmp = _getFieldValue("", items);

            FileDocument fileDocument = documentManager.uploadDocument(items);

            return NotesResponse.ok(fileDocument);

        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }


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

}
