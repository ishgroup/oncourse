package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Promotion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public interface PromotionApi  {

    @GET
    @Path("/promotion/{code}")
    @Produces({ "application/json" })
    Promotion getPromotion(@PathParam("code") String code);
}

