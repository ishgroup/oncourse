package ish.oncourse.willow.editor.service;

import java.util.List;
import ish.oncourse.willow.editor.model.MenuItem;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface MenuApi  {

    @GET
    @Path("/getMenuItems")
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> getMenuItems();

    @POST
    @Path("/saveMenuItems")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    List<MenuItem> saveMenuItems(List<MenuItem> menu);
}

