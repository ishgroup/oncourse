package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.Survey;
import ish.oncourse.services.course.GetRandomSurveys;
import ish.oncourse.services.course.GetSurveysByCourse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE;

public class CourseTestimonials {

    @Parameter
    @Property
    private Course course;

    @Property
    private List<Survey> surveys;

    @Property
    private Survey survey;

    @Property
    private boolean hasItems;

    private void beginRender() {
        List<Survey> allSurveys = GetSurveysByCourse.valueOf(course.getObjectContext(), course, SHARED_CACHE, Survey.class.getSimpleName()).get();
        surveys = GetRandomSurveys.valueOf(allSurveys, 3).get();
        hasItems = surveys.size() > 0;
    }
}
