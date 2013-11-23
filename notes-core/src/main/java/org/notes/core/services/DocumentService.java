package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.Document;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/document")
public class DocumentService {

    @Inject
    private DocumentManager documentManager;

    public DocumentManager getDocumentManager() {
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
