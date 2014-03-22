package org.notes.proxy.service;

import org.notes.common.configuration.NotesInterceptors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.io.IOException;

@NotesInterceptors
@Path("/proxy")
public class ProxyService {

    @HEAD
    @Path("/{fileId}")
    public void doHead(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context, @PathParam("fileId") String fileId)
            throws ServletException, IOException {
        // Process request without content.
        processRequest(request, response, context, fileId, false);
    }

    /**
     * Process GET request.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse).
     */
    @GET
    @Path("/{fileId}")
    public void doGet(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context, @PathParam("fileId") String fileId)
            throws ServletException, IOException {
        // Process request with content.
        processRequest(request, response, context, fileId, true);
    }

    private void processRequest
            (HttpServletRequest request, HttpServletResponse response, ServletContext context, String fileIdString, boolean content)
            throws IOException {
//        http://proxies.xhaus.com/java/
    }

}
