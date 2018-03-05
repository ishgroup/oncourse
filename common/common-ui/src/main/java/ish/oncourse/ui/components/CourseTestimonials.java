package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.Survey;
import ish.oncourse.services.survey.GetRandomSurveys;
import ish.oncourse.services.survey.GetSurveysByCourse;
import ish.oncourse.services.survey.GetWebVisibleSurveys;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

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
        List<Survey> allSurveys = GetSurveysByCourse.valueOf(course.getObjectContext(), course).get();
        surveys = GetRandomSurveys.valueOf(GetWebVisibleSurveys.valueOf(allSurveys).get(), 3).get();
        hasItems = surveys.size() > 0;
    }
}
