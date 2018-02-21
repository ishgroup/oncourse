package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.Layout;
import ish.oncourse.willow.editor.v1.model.Theme;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface ThemeApi  {

    @POST
    @Path("/v1/theme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme createTheme();

    @DELETE
    @Path("/v1/theme/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deleteTheme(@PathParam("id") String id);

    @GET
    @Path("/v1/layout")
    @Produces({ "application/json" })
    @AuthFilter
    List<Layout> getLayouts();

    @GET
    @Path("/v1/theme")
    @Produces({ "application/json" })
    @AuthFilter
    List<Theme> getThemes();

    @PUT
    @Path("/v1/theme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme updateTheme(Theme saveThemeRequest);
}

