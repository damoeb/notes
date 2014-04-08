package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.SessionData;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.User;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.endpoints.request.AuthParams;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.AuthenticationService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@NotesInterceptors
@Path("/auth")
public class AuthenticationEndpoint {

    public static final String USER_SETTINGS_SESSION_KEY = "user-settings";

    @Inject
    private AuthenticationService authenticationService;

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse register(
            AuthParams auth
    ) {
        try {
            User user = authenticationService.register(auth.getUsername(), auth.getPassword(), auth.getEmail());
            return NotesResponse.ok(user);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @POST
    @MethodCache
    @ServiceMetric
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse login(
            AuthParams auth,
            @Context HttpServletRequest request
    ) {
        try {
            SessionData settings = authenticationService.authenticate(auth.getUsername(), auth.getPassword());
            User user = settings.getUser();
            user.setFolders(null);
            user.setDocuments(null);
            user.setDatabases(null);

            for (StandardDatabase d : settings.getDatabases()) {
                d.setActiveFolder(null);
            }

            settings = getDomObjSession(settings);

            request.getSession().setAttribute(USER_SETTINGS_SESSION_KEY, settings);
            return NotesResponse.ok(settings);

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/logout")
    public NotesResponse logout(
            @Context HttpServletRequest request
    ) {
        try {
            HttpSession session = request.getSession();
            if (session == null) {
                return NotesResponse.error(new IllegalAccessException("no session found"));
            } else {
                session.invalidate();
                return NotesResponse.ok();
            }

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @MethodCache
    @ServiceMetric
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse settings(
            @Context HttpServletRequest request
    ) {
        try {
            HttpSession session = request.getSession();
            if (session == null) {
                return NotesResponse.error(new IllegalAccessException("no session found"));
            } else {
                SessionData settings = (SessionData) session.getAttribute(USER_SETTINGS_SESSION_KEY);
                if (settings == null) {
                    return NotesResponse.error(new IllegalAccessException("user not logged in"));
                }

                return NotesResponse.ok(settings);
            }

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    // --

    private SessionData getDomObjSession(final SessionData settings) {
        return new SessionData() {
            @Override
            public User getUser() {
                return settings.getUser();
            }

            @Override
            public Set<StandardDatabase> getDatabases() {
                return settings.getDatabases();
            }

            @Override
            public void setDatabases(Set<StandardDatabase> databases) {
            }

            @Override
            public void setUser(User user) {
            }
        };
    }
}
