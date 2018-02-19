package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.redirect.Redirects;

import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface RedirectApi  {

    @GET
    @Path("/v1/redirect.list")
    @Produces({ "application/json" })
    @AuthFilter
    Redirects redirectListGet();

    @POST
    @Path("/v1/redirect.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Redirects redirectUpdatePost(Redirects redirectSettingsRequest);
}

