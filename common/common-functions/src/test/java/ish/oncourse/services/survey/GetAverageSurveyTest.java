package ish.oncourse.services.survey;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAverageSurveyTest {

    @Test
    public void testSurveyInit() {
        ObjectContext context = mock(ObjectContext.class);
        CourseClass courseClass = mock(CourseClass.class);

        Survey result = GetAverageSurvey.valueOf(context, courseClass).initTransportObject();

        assertEquals((Integer) 0, result.getCourseScore());
        assertEquals((Integer) 0, result.getVenueScore());
        assertEquals((Integer) 0, result.getTutorScore());
        assertEquals((Integer) 0, result.getNetPromoterScore());
        assertEquals((Integer) 0, result.getNetPromoterScore());
        assertEquals(SurveyVisibility.TESTIMONIAL, result.getVisibility());
    }

    @Test
    public void testCountAverageValues () {
        ObjectContext context = mock(ObjectContext.class);
        CourseClass courseClass = mock(CourseClass.class);

        Survey survey = GetAverageSurvey.valueOf(context, courseClass).initTransportObject();

        GetAverageSurvey.valueOf(context, courseClass).calculateAverage(survey, createAverageSurveys());

        assertEquals((Integer) 3, survey.getCourseScore());
        assertEquals((Integer) 2, survey.getVenueScore());
        assertEquals((Integer) 3, survey.getTutorScore());
        assertEquals((Integer) 5, survey.getNetPromoterScore());
    }

    private List<Survey> createAverageSurveys() {
        List<Survey> surveys = new ArrayList<>();

        Survey s = mock(Survey.class);
        when(s.getCourseScore()).thenReturn(4);
        when(s.getVenueScore()).thenReturn(3);
        when(s.getTutorScore()).thenReturn(2);
        when(s.getNetPromoterScore()).thenReturn(1);

        surveys.add(s);

        s = mock(Survey.class);
        when(s.getCourseScore()).thenReturn(2);
        when(s.getVenueScore()).thenReturn(1);
        when(s.getTutorScore()).thenReturn(4);
        when(s.getNetPromoterScore()).thenReturn(10);

        surveys.add(s);
        return surveys;
    }
}
