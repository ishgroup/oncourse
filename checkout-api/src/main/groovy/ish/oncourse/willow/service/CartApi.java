package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface CartApi  {

    @POST
    @Path("/v1/cart/{id}")
    @Produces({ "application/json" })
    void create(@PathParam("id") String id, String checkout);

    @DELETE
    @Path("/v1/cart/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void delete(@PathParam("id") String id);

    @GET
    @Path("/v1/cart/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    String get(@PathParam("id") String id);
}

