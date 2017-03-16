package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Error;
import ish.oncourse.willow.model.InlineResponse404;
import ish.oncourse.willow.model.Promotion;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface PromotionApi  {

    @GET
    @Path("/promotion/{code}")
    @Produces({ "application/json" })
    Promotion getPromotion(@PathParam("code") String code);
}

