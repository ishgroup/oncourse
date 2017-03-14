package ish.oncourse.services.courseclass.functions;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Returns active and visible course classes by path
 * Created by pavel on 3/13/17.
 */
public class GetCourseClassByFullCode {

    private ObjectContext context;
    private College college;
    private String code;

    private GetCourseClassByFullCode() {
    }

    public static GetCourseClassByFullCode valueOf(ObjectContext context, College college, String code) {
        GetCourseClassByFullCode result = new GetCourseClassByFullCode();
        result.context = context;
        result.college = college;
        result.code = code;
        return result;
    }

    public CourseClass get() {
        String[] parts = code.split("-");
        // courseClass code has format "course.code-courseClass.code"
        if (parts.length < 2) {
            return null;
        }
        String courseCode = parts[0];
        String courseClassCode = parts[1];

        ObjectSelect<CourseClass> query = ObjectSelect.query(CourseClass.class, ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, college)
                .andExp(ExpressionFactory.matchExp(CourseClass.IS_ACTIVE_PROPERTY, true))
                .andExp(getSearchStringPropertyQualifier(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY, courseCode))
                .andExp(getSearchStringPropertyQualifier(CourseClass.CODE_PROPERTY, courseClassCode)));

        return ApplyCourseClassCacheSettings.valueOf(query).apply().selectOne(context);
    }

    private Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
        return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
    }

    private Expression getSiteQualifier() {
        return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, college).andExp(
                ExpressionFactory.matchExp(CourseClass.IS_ACTIVE_PROPERTY, true));
    }
}
