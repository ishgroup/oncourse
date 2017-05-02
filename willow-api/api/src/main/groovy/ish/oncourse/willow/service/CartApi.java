package ish.oncourse.willow.service;

import ish.oncourse.willow.model.web.Token;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public interface CartApi  {

    @POST
    @Path("/cart")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Token cartPost();
}

