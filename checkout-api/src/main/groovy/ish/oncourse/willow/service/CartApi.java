package ish.oncourse.willow.service;

import javax.ws.rs.*;

@Path("/")
public interface CartApi  {

    @POST
    @Path("/v1/cart/{id}")
    @Produces({ "application/json" })
    @CollegeInfo
    void create(@PathParam("id") String id, String checkout);

    @DELETE
    @Path("/v1/cart/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    void delete(@PathParam("id") String id);

    @GET
    @Path("/v1/cart/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    String get(@PathParam("id") String id);
}

