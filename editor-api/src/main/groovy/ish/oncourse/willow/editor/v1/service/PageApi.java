package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.Page;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface PageApi  {

    @POST
    @Path("/v1/page")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page createPage();

    @DELETE
    @Path("/v1/page/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deletePage(@PathParam("id") String id);

    @GET
    @Path("/v1/page")
    @Produces({ "application/json" })
    @AuthFilter
    List<Page> getPages(@QueryParam("pageUrl")String pageUrl);

    @PUT
    @Path("/v1/page")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Page updatePage(Page pageParams);
}

