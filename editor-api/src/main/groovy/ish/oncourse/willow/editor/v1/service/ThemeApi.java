package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Layout;
import ish.oncourse.willow.editor.v1.model.Theme;

import java.util.List;
import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface ThemeApi  {

    @GET
    @Path("/v1/layout.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Layout> layoutListGet();

    @POST
    @Path("/v1/theme.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme themeCreatePost();

    @POST
    @Path("/v1/theme.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void themeDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/v1/theme.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Theme> themeListGet();

    @POST
    @Path("/v1/theme.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme themeUpdatePost(Theme saveThemeRequest);
}

