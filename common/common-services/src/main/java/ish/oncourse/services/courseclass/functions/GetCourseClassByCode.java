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
public class GetCourseClassByCode {

    private ObjectContext context;
    private College college;
    private String code;

    private GetCourseClassByCode() {
    }

    /**
     * Create instance
     * @param context
     * @param college
     * @param code
     * @return
     */
    public static GetCourseClassByCode valueOf(ObjectContext context, College college, String code) {
        GetCourseClassByCode result = new GetCourseClassByCode();
        result.context = context;
        result.college = college;
        result.code = code;
        return result;
    }

    /**
     * Return active webvisible course class by code
     * @return
     */
    public CourseClass get() {
        String[] parts = code.split("-");
        // courseClass code has format "course.code-courseClass.code"
        if (parts.length < 2) {
            return null;
        }
        String courseCode = parts[0];
        String courseClassCode = parts[1];

        ObjectSelect<CourseClass> query = ObjectSelect.query(CourseClass.class, CourseClass.COLLEGE.eq(college)
                .andExp(CourseClass.IS_ACTIVE.isTrue()
                        .orExp(CourseClass.IS_ACTIVE.isFalse().andExp(CourseClass.CANCELLED.isTrue().andExp(CourseClass.IS_WEB_VISIBLE.isTrue()))))
                .andExp(CourseClass.CODE.likeIgnoreCase(courseClassCode))
                .andExp(CourseClass.COURSE.dot(Course.CODE).likeIgnoreCase(courseCode)));

        return ApplyCourseClassCacheSettings.valueOf(query).apply().selectOne(context);
    }
}
