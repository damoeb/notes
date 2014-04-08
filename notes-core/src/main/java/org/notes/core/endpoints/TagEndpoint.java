package org.notes.core.endpoints;

import org.notes.common.cache.MethodCache;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.domain.TextDocument;
import org.notes.core.endpoints.internal.NotesResponse;
import org.notes.core.metric.ServiceMetric;
import org.notes.core.services.DocumentService;
import org.notes.core.services.TagService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@NotesInterceptors
@Path("/tag")
public class TagEndpoint {

//  --------------------------------------------------------------------------------------------------------------------

    @Inject
    private TagService tagService;

    @Inject
    private DocumentService documentService;

    @GET
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/recommend/{documentId}")
    public NotesResponse getRecommendations(@PathParam("documentId") long documentId) {
        try {
            return NotesResponse.ok(tagService.getRecommendations(documentService.getDocument(documentId)));

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }

    @GET
    @ServiceMetric
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/recommend")
    public NotesResponse getRecommendations(TextDocument document) {
        try {
            return NotesResponse.ok(tagService.getRecommendations(document));

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
            return NotesResponse.ok(tagService.getUsersTagNetwork());

        } catch (Throwable t) {
            return NotesResponse.error(t);
        }
    }
}
