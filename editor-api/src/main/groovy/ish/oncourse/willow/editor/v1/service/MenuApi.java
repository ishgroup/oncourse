package ish.oncourse.willow.editor.v1.service;

import java.util.List;
import ish.oncourse.willow.editor.v1.model.MenuItem;

import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface MenuApi  {

    @GET
    @Path("/v1/menu.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> menuListGet();

    @POST
    @Path("/v1/menu.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> menuUpdatePost(List<MenuItem> menu);
}

