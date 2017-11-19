package ish.oncourse.willow.editor.service;

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

    @POST
    @Path("/addTheme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme addTheme();

    @POST
    @Path("/deleteTheme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deleteTheme(Double id);

    @GET
    @Path("/getLayouts")
    @Produces({ "application/json" })
    @AuthFilter
    List<Theme> getLayouts();

    @GET
    @Path("/getThemes")
    @Produces({ "application/json" })
    @AuthFilter
    List<Theme> getThemes();

    @POST
    @Path("/saveTheme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Theme saveTheme(Theme saveThemeRequest);
}

