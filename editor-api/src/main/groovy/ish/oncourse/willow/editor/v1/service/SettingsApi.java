package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.settings.SkillsOnCourseSettings;
import ish.oncourse.willow.editor.v1.model.settings.WebsiteSettings;

import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface SettingsApi  {

    @GET
    @Path("/v1/settings.skillsOnCourse.get")
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings settingsSkillsOnCourseGetGet();

    @POST
    @Path("/v1/settings.skillsOnCourse.set")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    SkillsOnCourseSettings settingsSkillsOnCourseSetPost(SkillsOnCourseSettings skillsOnCourseSettingsRequest);

    @GET
    @Path("/v1/settings.website.get")
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings settingsWebsiteGetGet();

    @POST
    @Path("/v1/settings.website.set")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    WebsiteSettings settingsWebsiteSetPost(WebsiteSettings websiteSettingsRequest);
}

