package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.SkillsOnCourseSettings;
import ish.oncourse.willow.editor.v1.model.WebsiteSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface SettingsApi  {

    @GET
    @Path("/v1/settings/skillsOnCourse")
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings getSkillsOnCourseSettings();

    @GET
    @Path("/v1/settings/website")
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings getWesiteSettings();

    @PUT
    @Path("/v1/settings/skillsOnCourse")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings updateSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest);

    @PUT
    @Path("/v1/settings/website")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings updateWesiteSettings(WebsiteSettings websiteSettingsRequest);
}

