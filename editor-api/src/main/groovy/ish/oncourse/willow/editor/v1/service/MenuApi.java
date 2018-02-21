package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import java.util.List;
import ish.oncourse.willow.editor.v1.model.MenuItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface MenuApi  {

    @GET
    @Path("/v1/menu")
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> getMenus();

    @PUT
    @Path("/v1/menu")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> updateMenus(List<MenuItem> menu);
}

