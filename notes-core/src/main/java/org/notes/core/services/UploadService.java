package org.notes.core.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.TextDocumentManager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@NotesInterceptors
@Path("/upload")
public class UploadService {

    @Inject
    private TextDocumentManager documentManager;

    // -- UPLOAD --

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
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
            List<File> files = new LinkedList<File>();

            //String tmp = _getFieldValue("", items);

            for (FileItem item : items) {

                if (!item.isFormField()) {

                    /*
                     todo
                      move file with proper mime type from tmp to repository
                      */
                    //RepositoryFile repositoryFile = fileManager.storeInRepository(item);
                    //files.add(documentManager.addAttachmentToNote(item.getName(), repositoryFile, note));
                }
            }

            // todo create document


            return NotesResponse.ok(files);

        } catch (Throwable t) {
            t.printStackTrace();
            return NotesResponse.error(t);
        }
    }

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
    private String _getFieldValue(String fieldName, List<FileItem> items) throws NotesException {
        for (FileItem item : items) {

            if (item.isFormField()) {

                String someFieldName = item.getFieldName();
                if (StringUtils.equalsIgnoreCase(someFieldName, fieldName)) {
                    return item.getString();
                }
            }
        }

        return null;
    }
}
