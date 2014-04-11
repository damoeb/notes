package org.notes.core.endpoints;

import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interceptors.Bouncer;
import org.notes.core.services.ProxyService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.io.IOException;

@NotesInterceptors
@Path("/proxy")
public class ProxyEndpoint {

    @Inject
    private ProxyService proxyService;

    @HEAD
    @Bouncer
    public void doHead(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("url") String url)
            throws ServletException, IOException {
        try {
            proxyService.proxyRequest(request, response, url);

        } catch (NotesException e) {
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * Process GET request.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse).
     */
    @GET
    @Bouncer
    public void doGet(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("url") String url)
            throws ServletException, IOException {
        try {
            proxyService.proxyRequest(request, response, url);

        } catch (NotesException e) {
            throw new ServletException(e.getMessage());
        }
    }
}
