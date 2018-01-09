package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE;

public class GetSurveysByCourse {

    private ObjectContext context;
    private Course course;
    private QueryCacheStrategy cacheStrategy;
    private String cacheGroup;

    private GetSurveysByCourse() {}

    public static GetSurveysByCourse valueOf(ObjectContext context, Course course, QueryCacheStrategy cacheStrategy, String cacheGroup) {
        GetSurveysByCourse getter = new GetSurveysByCourse();
        getter.context = context;
        getter.course = course;
        getter.cacheStrategy = cacheStrategy;
        getter.cacheGroup = cacheGroup;
        return getter;
    }

    public List<Survey> get() {
        ObjectSelect select = ObjectSelect.query(Survey.class)
                .where(Survey.COLLEGE.eq(course.getCollege())
                        .andExp(Survey.ENROLMENT
                                .dot(Enrolment.COURSE_CLASS)
                                .dot(CourseClass.COURSE)
                                .eq(course)));
        if (cacheStrategy != null) {
            if (cacheGroup != null) {
                select.cacheStrategy(cacheStrategy, cacheGroup);
            } else {
                select.cacheStrategy(cacheStrategy);
            }
        }
        return select.select(context);
    }
}
