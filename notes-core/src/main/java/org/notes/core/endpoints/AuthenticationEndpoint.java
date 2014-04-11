package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.User;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.endpoints.request.AuthParams;
import org.notes.core.interceptors.Bouncer;
import org.notes.core.metric.PerformanceLogger;
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
    @PerformanceLogger
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
    @PerformanceLogger
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse login(
            AuthParams auth,
            @Context HttpServletRequest request
    ) {
        try {
            NotesSession settings = authenticationService.authenticate(auth.getUsername(), auth.getPassword());
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
    @PerformanceLogger
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
    @PerformanceLogger
    @Bouncer
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
                NotesSession settings = (NotesSession) session.getAttribute(USER_SETTINGS_SESSION_KEY);
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

    private NotesSession getDomObjSession(final NotesSession settings) {
        return new NotesSession() {
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
