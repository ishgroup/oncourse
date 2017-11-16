package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.common.CommonError;
import ish.oncourse.willow.editor.model.settings.CheckoutSettings;
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
    @Path("/getCheckoutSettings")
    @Produces({ "application/json" })
    CheckoutSettings getCheckoutSettings();

    @GET
    @Path("/getRedirectSettings")
    @Produces({ "application/json" })
    RedirectSettings getRedirectSettings();

    @GET
    @Path("/getSkillsOnCourseSettings")
    @Produces({ "application/json" })
    SkillsOnCourseSettings getSkillsOnCourseSettings();

    @GET
    @Path("/getWebsiteSettings")
    @Produces({ "application/json" })
    WebsiteSettings getWebsiteSettings();

    @POST
    @Path("/setCheckoutSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    CheckoutSettings setCheckoutSettings(CheckoutSettings saveCheckoutSettingsRequest);

    @POST
    @Path("/setRedirectSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    RedirectSettings setRedirectSettings(RedirectSettings redirectSettingsRequest);

    @POST
    @Path("/setSkillsOnCourseSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    SkillsOnCourseSettings setSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest);

    @POST
    @Path("/setWebsiteSettings")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    WebsiteSettings setWebsiteSettings(WebsiteSettings websiteSettingsRequest);
}

