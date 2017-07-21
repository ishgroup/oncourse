package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.Preferences;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface PreferenceApi  {

    @GET
    @Path("/getPreferences")
    @Produces({ "application/json" })
    @CollegeInfo
    Preferences getPreferences();
}

