package ish.oncourse.willow.service;

import ish.oncourse.willow.model.web.CourseClass;
import ish.oncourse.willow.model.web.CourseClassesParams;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/")
public interface CourseClassesApi  {

    @POST
    @Path("/classes")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams);
}

