package org.notes.core.services;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.TagManager;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.model.TextDocument;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/tag")
public class TagService {

//  --------------------------------------------------------------------------------------------------------------------

    @Inject
    private TagManager tagManager;

    @GET
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/recommend")
    public NotesResponse getRecommendations(TextDocument document) {
        try {

            return NotesResponse.ok(tagManager.getRecommendations(document));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }


    @GET
    @MethodCache
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/network")
    public NotesResponse getNetwork() {
        try {
            return NotesResponse.ok(tagManager.getTagNetwork());
        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

}
