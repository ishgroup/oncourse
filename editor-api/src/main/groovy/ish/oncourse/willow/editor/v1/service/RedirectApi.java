package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.Redirects;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface RedirectApi  {

    @GET
    @Path("/v1/redirect")
    @Produces({ "application/json" })
    @AuthFilter
    Redirects getRedirects();

    @PUT
    @Path("/v1/redirect")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Redirects updateRedirects(Redirects redirectSettingsRequest);
}

