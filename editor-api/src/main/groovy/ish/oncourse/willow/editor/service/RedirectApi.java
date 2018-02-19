package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.common.CommonError;
import ish.oncourse.willow.editor.model.redirect.RedirectSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface RedirectApi  {

    @GET
    @Path("/redirect.list")
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings redirectListGet();

    @POST
    @Path("/redirect.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings redirectUpdatePost(RedirectSettings redirectSettingsRequest);
}

