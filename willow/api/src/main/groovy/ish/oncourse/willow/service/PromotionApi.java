package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Promotion;

import javax.ws.rs.*;

@Path("/promotion")
public interface PromotionApi  {

    @GET
    @Path("{code}")
    @Produces({ "application/json" })
    Promotion getPromotion(@PathParam("code") String code);
}

