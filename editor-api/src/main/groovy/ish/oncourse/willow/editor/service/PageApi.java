package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Page;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface PageApi  {

    @POST
    @Path("/page.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page pageCreatePost();

    @POST
    @Path("/page.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void pageDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/page.get")
    @Produces({ "application/json" })
    @AuthFilter
    Page pageGetGet(@QueryParam("pageUrl")String pageUrl);

    @GET
    @Path("/page.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Page> pageListGet();

    @POST
    @Path("/page.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page pageUpdatePost(Page pageParams);
}

