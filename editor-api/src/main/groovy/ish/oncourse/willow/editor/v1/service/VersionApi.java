package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.Version;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface VersionApi  {

    @GET
    @Path("/v1/version")
    @Produces({ "application/json" })
    @AuthFilter
    List<Version> getVersions();

    @PATCH
    @Path("/v1/version/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void updateVersion(@PathParam("id") String id, Version version);
}

