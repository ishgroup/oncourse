package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.common.CommonError;
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
    @Path("/settings.skillsOnCourse.get")
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings settingsSkillsOnCourseGetGet();

    @POST
    @Path("/settings.skillsOnCourse.set")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings settingsSkillsOnCourseSetPost(SkillsOnCourseSettings skillsOnCourseSettingsRequest);

    @GET
    @Path("/settings.website.get")
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings settingsWebsiteGetGet();

    @POST
    @Path("/settings.website.set")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings settingsWebsiteSetPost(WebsiteSettings websiteSettingsRequest);
}

