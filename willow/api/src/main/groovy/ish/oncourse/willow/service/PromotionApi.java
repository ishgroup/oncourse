package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Promotion;

import javax.ws.rs.*;

@Path("/")
public interface PromotionApi  {

    @GET
    @Path("/promotion/{code}")
    @Produces({ "application/json" })
    Promotion getPromotion(@PathParam("code") String code);
}

