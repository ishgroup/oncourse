package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.redirect.RedirectSettings;

import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface RedirectApi  {

    @GET
    @Path("/v1/redirect.list")
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings redirectListGet();

    @POST
    @Path("/v1/redirect.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings redirectUpdatePost(RedirectSettings redirectSettingsRequest);
}

