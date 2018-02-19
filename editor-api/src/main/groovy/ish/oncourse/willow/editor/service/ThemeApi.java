package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Layout;
import ish.oncourse.willow.editor.model.Theme;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface ThemeApi  {

    @GET
    @Path("/layout.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Layout> layoutListGet();

    @POST
    @Path("/theme.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme themeCreatePost();

    @POST
    @Path("/theme.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void themeDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/theme.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Theme> themeListGet();

    @POST
    @Path("/theme.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme themeUpdatePost(Theme saveThemeRequest);
}

