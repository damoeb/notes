package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.AuthenticationManager;
import org.notes.core.metric.ServiceMetric;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/session")
public class SessionService {

    @Inject
    private AuthenticationManager authenticationManager;

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse login(
            @PathParam("username") String username,
            @PathParam("password") String password
    ) throws Exception {
        try {
            return NotesResponse.ok(authenticationManager.authenticate(username, password));
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/logout")
    public NotesResponse logout(
            @Context HttpServletRequest request
    ) throws Exception {
        try {
            HttpSession session = request.getSession();
            if (session == null) {
                return NotesResponse.error(new IllegalAccessException("no session available"));
            } else {
                session.invalidate();
                return NotesResponse.ok();
            }

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

}
