package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Version;

import java.util.List;
import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface VersionApi  {

    @POST
    @Path("/v1/version.draft.publish")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void versionDraftPublishPost();

    @POST
    @Path("/v1/version.draft.set/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void versionDraftSetIdPost(@PathParam("id") String id);

    @GET
    @Path("/v1/version.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Version> versionListGet();
}

