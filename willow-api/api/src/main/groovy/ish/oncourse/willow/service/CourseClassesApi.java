package ish.oncourse.willow.service;

import ish.oncourse.willow.model.CourseClass;
import ish.oncourse.willow.model.CourseClassesParams;
import ish.oncourse.willow.model.Error;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface CourseClassesApi  {

    @POST
    @Path("/classes")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams);
}

