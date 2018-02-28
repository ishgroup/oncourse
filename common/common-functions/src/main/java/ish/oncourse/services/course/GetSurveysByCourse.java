package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

public class GetSurveysByCourse {

    private ObjectContext context;
    private Course course;
    private QueryCacheStrategy cacheStrategy = LOCAL_CACHE;

    private GetSurveysByCourse() {}

    public static GetSurveysByCourse valueOf(ObjectContext context, Course course) {
        GetSurveysByCourse getter = new GetSurveysByCourse();
        getter.context = context;
        getter.course = course;
        return getter;
    }

    public GetSurveysByCourse cacheStrategy(QueryCacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
        return this;
    }

    public List<Survey> get() {
        ObjectSelect select = ObjectSelect.query(Survey.class)
                .where(Survey.COLLEGE.eq(course.getCollege())
                        .andExp(Survey.ENROLMENT
                                .dot(Enrolment.COURSE_CLASS)
                                .dot(CourseClass.COURSE)
                                .eq(course)))
                .cacheStrategy(cacheStrategy, Survey.class.getSimpleName());
        return select.select(context);
    }
}
