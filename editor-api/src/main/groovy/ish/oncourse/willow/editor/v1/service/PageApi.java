package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Page;

import java.util.List;
import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface PageApi  {

    @POST
    @Path("/v1/page.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page pageCreatePost();

    @POST
    @Path("/v1/page.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void pageDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/v1/page.get")
    @Produces({ "application/json" })
    @AuthFilter
    Page pageGetGet(@QueryParam("pageUrl")String pageUrl);

    @GET
    @Path("/v1/page.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Page> pageListGet();

    @POST
    @Path("/v1/page.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page pageUpdatePost(Page pageParams);
}

