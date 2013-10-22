package org.notes.core.services;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.json.JSONArray;
import org.json.JSONObject;
import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;
import org.notes.core.model.User;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@NotesInterceptors
@Path("/session")
public class SessionService {

    @Inject
    private UserManager userManager;

    @Inject
    private FolderManager folderManager;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    class SessionWrapper {
        private User user;
        private Collection<Folder> notebooks;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Collection<Folder> getNotebooks() {
            return notebooks;
        }

        public void setNotebooks(Collection<Folder> notebooks) {
            this.notebooks = notebooks;
        }
    }

    @GET
    @MethodCache
    @Produces(MediaType.APPLICATION_JSON)
    public NotesResponse getSession(
    ) throws Exception {

        User user = userManager.getUser(userManager.getUserId());

        SessionWrapper wrapper = new SessionWrapper();
        wrapper.setUser(user);
        //wrapper.setNotebooks(folderManager.getChildren(user.getRootId()));

        return NotesResponse.ok(wrapper);
    }
}

