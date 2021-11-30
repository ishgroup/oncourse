package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.web.Course;
import ish.oncourse.willow.model.web.CourseClass;
import ish.oncourse.willow.model.web.CourseClassesParams;
import ish.oncourse.willow.model.web.CoursesParams;

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
    @Path("/v1/course/classes/{courseId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<CourseClass> getAvailableClasses(@PathParam("courseId") String courseId);

    @POST
    @Path("/v1/classes")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams);

    @POST
    @Path("/v1/courses")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<Course> getCourses(CoursesParams coursesParams);
}

