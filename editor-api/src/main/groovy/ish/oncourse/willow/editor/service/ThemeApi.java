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
    Theme addTheme();

    @POST
    @Path("/deleteTheme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void deleteTheme();

    @GET
    @Path("/getLayouts")
    @Produces({ "application/json" })
    Theme getLayouts();

    @GET
    @Path("/getThemes")
    @Produces({ "application/json" })
    Theme getThemes();

    @POST
    @Path("/saveTheme")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Theme saveTheme(Theme saveThemeRequest);
}

