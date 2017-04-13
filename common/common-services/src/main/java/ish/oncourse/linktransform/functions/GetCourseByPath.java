package ish.oncourse.linktransform.functions;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.functions.GetCourseByCode;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by pavel on 3/27/17.
 */
public class GetCourseByPath {
    private static final String LEFT_SLASH_CHARACTER = "/";
    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private WebSite webSite;
    private String path;

    private GetCourseByPath() {
    }

    public static GetCourseByPath valueOf(ObjectContext context, WebSite webSite, String path) {
        GetCourseByPath result = new GetCourseByPath();
        result.context = context;
        result.webSite= webSite;
        result.path = path;
        return result;
    }

    /**
     * Receives available CourseClass item by path. If course exists for current college, is web visible and have any
     * attached tag returns CourseClass object
     *
     * @return CourseClass object
     */
    public Course get() {
        Course course = null;
        String courseCode = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
        if (courseCode != null) {
            course = GetCourseByCode.valueOf(context, webSite.getCollege(), courseCode).get();
        }
        if (course != null && GetEntityAvailabilityByTag.valueOf(context, webSite, course).get()) {
            return course;
        } else {
            return null;
        }
    }
}
