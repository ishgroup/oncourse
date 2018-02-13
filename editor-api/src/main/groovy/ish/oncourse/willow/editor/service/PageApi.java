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
    @Path("/addPage")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page addPage();

    @POST
    @Path("/deletePage/{pageNumber}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deletePage(@PathParam("pageNumber") String pageNumber);

    @GET
    @Path("/getPageByUrl")
    @Produces({ "application/json" })
    @AuthFilter
    Page getPageByUrl(@QueryParam("pageUrl")String pageUrl);

    @GET
    @Path("/getPages")
    @Produces({ "application/json" })
    @AuthFilter
    List<Page> getPages();

    @POST
    @Path("/savePage")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page savePage(Page pageParams);
}

