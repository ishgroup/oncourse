package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Model200;
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
    CommonError addPage();

    @POST
    @Path("/deletePage")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void deletePage();

    @GET
    @Path("/getPageByUrl/{pageUrl}")
    @Produces({ "application/json" })
    Page getPageByUrl(@PathParam("pageUrl") String pageUrl);

    @GET
    @Path("/getPageRender/{pageId}")
    @Produces({ "application/json" })
    Model200 getPageRender(@PathParam("pageId") String pageId);

    @GET
    @Path("/getPages")
    @Produces({ "application/json" })
    List<Page> getPages();

    @POST
    @Path("/savePage")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Page savePage(Page pageParams);
}

