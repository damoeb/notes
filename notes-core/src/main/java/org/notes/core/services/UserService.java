package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.AuthenticationManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.User;
import org.notes.core.model.UserSettings;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/user")
public class UserService {

    public static final String USER_SETTINGS = "usersettings";

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private UserManager userManager;

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse login(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("email") String email
    ) throws Exception {
        try {
            User user = userManager.registerUser(username, password, email);
            return NotesResponse.ok(user);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse login(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @Context HttpServletRequest request
    ) throws Exception {
        try {
            UserSettings settings = authenticationManager.authenticate(username, password);
            request.getSession().setAttribute(USER_SETTINGS, settings);
            return NotesResponse.ok(settings);
        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/logout")
    public NotesResponse logout(
            @Context HttpServletRequest request
    ) throws Exception {
        try {
            HttpSession session = request.getSession();
            if (session == null) {
                return NotesResponse.error(new IllegalAccessException("no session found"));
            } else {
                session.invalidate();
                return NotesResponse.ok();
            }

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse current(
            @Context HttpServletRequest request
    ) throws Exception {
        try {
            HttpSession session = request.getSession();
            if (session == null) {
                return NotesResponse.error(new IllegalAccessException("no session found"));
            } else {
                UserSettings settings = (UserSettings) session.getAttribute(USER_SETTINGS);
                if (settings == null) {
                    return NotesResponse.error(new IllegalAccessException("user not logged in"));
                }

                return NotesResponse.ok(settings);
            }

        } catch (Exception e) {
            return NotesResponse.error(e);
        }
    }
}
