package ish.oncourse.linktransform.functions;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.functions.GetCourseClassByFullCode;
import ish.oncourse.services.course.functions.ApplyCourseCacheSettings;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Functional class for CourseClass processing
 * Created by pavel on 3/9/17.
 */
public class GetCourseClassByPath {

    private static final String LEFT_SLASH_CHARACTER = "/";
    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private College college;
    private String path;

    private GetCourseClassByPath() {
    }

    public static GetCourseClassByPath valueOf(ObjectContext context, College college, String path) {
        GetCourseClassByPath result = new GetCourseClassByPath();
        result.context = context;
        result.college = college;
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
            courseClass = GetCourseClassByFullCode.valueOf(context, college, courseClassCode).get();
        }
        if (courseClass != null &&  availableByTag(courseClass.getCourse())) {
            return courseClass;
        } else {
            return null;
        }
    }

    /**
     * Check any tag availability for course
     * @param course course object
     * @return <code>true<code/> if any tag assigned to course object, <code>false</code> otherwise
     */
    protected boolean availableByTag(Course course) {
        List<Tag> results = null;
        ObjectSelect query = ObjectSelect.query(Tag.class).and(getQualifier(course));

        try {
            results = context.performQuery(
                    ApplyCourseCacheSettings.valueOf(query).apply());
        } catch (Exception e) {
            logger.error("Query resulted in Exception thrown. Query: {}", query, e);
            //TODO: Should the exception be rethrown to indicate error condition to the client code?
        }
        if (results != null && !results.isEmpty())
            return true;
        return false;
    }

    /**
     * Expression which select all tags assigned to course
     * @param course course object
     * @return expression
     */
    protected Expression getQualifier (Course course){
        return Tag.COLLEGE.eq(course.getCollege()).andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(course.getClass().getSimpleName())
                .andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(course.getId())));
    }
}
