package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Version;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface VersionApi  {

    @POST
    @Path("/version.draft.publish")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void versionDraftPublishPost();

    @POST
    @Path("/version.draft.set/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void versionDraftSetIdPost(@PathParam("id") String id);

    @GET
    @Path("/version.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Version> versionListGet();
}

