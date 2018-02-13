package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.common.CommonError;
import ish.oncourse.willow.editor.model.settings.RedirectSettings;
import ish.oncourse.willow.editor.model.settings.SkillsOnCourseSettings;
import ish.oncourse.willow.editor.model.settings.WebsiteSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface SettingsApi  {

    @GET
    @Path("/getRedirectSettings")
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings getRedirectSettings();

    @GET
    @Path("/getSkillsOnCourseSettings")
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings getSkillsOnCourseSettings();

    @GET
    @Path("/getWebsiteSettings")
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings getWebsiteSettings();

    @POST
    @Path("/setRedirectSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    RedirectSettings setRedirectSettings(RedirectSettings redirectSettingsRequest);

    @POST
    @Path("/setSkillsOnCourseSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings setSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest);

    @POST
    @Path("/setWebsiteSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings setWebsiteSettings(WebsiteSettings websiteSettingsRequest);
}

