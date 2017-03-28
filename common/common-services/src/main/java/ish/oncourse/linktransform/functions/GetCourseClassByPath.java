package ish.oncourse.linktransform.functions;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.functions.GetCourseClassByCode;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Functional class for CourseClass processing
 * Created by pavel on 3/9/17.
 */
public class GetCourseClassByPath {

    private static final String LEFT_SLASH_CHARACTER = "/";
    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private WebSite webSite;
    private String path;

    private GetCourseClassByPath() {
    }

    public static GetCourseClassByPath valueOf(ObjectContext context, WebSite webSite, String path) {
        GetCourseClassByPath result = new GetCourseClassByPath();
        result.context = context;
        result.webSite = webSite;
        result.path = path;
        return result;
    }

    /**
     * Receives available CourseClass item by path. If course exists for current college, is web visible and have any
     * attached tag returns CourseClass object
     * @return CourseClass object
     */
    public CourseClass get() {
        CourseClass courseClass = null;
        String courseClassCode = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
        if (courseClassCode != null) {
            courseClass = GetCourseClassByCode.valueOf(context, webSite.getCollege(), courseClassCode).get();
        }
        if (courseClass != null && GetEntityAvailabilityByTag.valueOf(context, webSite, courseClass.getCourse()).get()) {
            return courseClass;
        } else {
            return null;
        }
    }
}
