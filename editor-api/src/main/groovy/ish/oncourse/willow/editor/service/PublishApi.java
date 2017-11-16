package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.SetVersionRequest;
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
public interface PublishApi  {

    @GET
    @Path("/getVersions")
    @Produces({ "application/json" })
    List<Version> getVersions();

    @POST
    @Path("/publish")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void publish();

    @POST
    @Path("/setVersion")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void setVersion(SetVersionRequest setVersionRequest);
}

