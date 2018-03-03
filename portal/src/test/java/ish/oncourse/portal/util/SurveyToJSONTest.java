package ish.oncourse.portal.util;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.Survey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SurveyToJSONTest {

    private static final String TUTOR_EXPECTED = "{\n" +
            "  \"courseScore\" : 5,\n" +
            "  \"tutorScore\" : 4,\n" +
            "  \"venueScore\" : 3,\n" +
            "  \"comment\" : \"comment\",\n" +
            "  \"visibility\" : 1,\n" +
            "  \"netPromoterScore\" : 2,\n" +
            "  \"readOnly\" : true\n" +
            "}";

    private static final String NOT_TUTOR = "{\n" +
            "  \"courseScore\" : 5,\n" +
            "  \"tutorScore\" : 4,\n" +
            "  \"venueScore\" : 3,\n" +
            "  \"comment\" : \"comment\",\n" +
            "  \"visibility\" : 1,\n" +
            "  \"netPromoterScore\" : 2\n" +
            "}";

    @Test
    public void testIsTutor() {
        assertEquals(TUTOR_EXPECTED, SurveyToJSON.valueOf(getSurvey(), true).get().toString());
    }

    @Test
    public void testNotTutor() {
        assertEquals(NOT_TUTOR, SurveyToJSON.valueOf(getSurvey(), false).get().toString());
    }

    private Survey getSurvey() {
        Survey s = mock(Survey.class);
        when(s.getCourseScore()).thenReturn(5);
        when(s.getTutorScore()).thenReturn(4);
        when(s.getVenueScore()).thenReturn(3);
        when(s.getNetPromoterScore()).thenReturn(2);
        when(s.getComment()).thenReturn("comment");
        when(s.getVisibility()).thenReturn(SurveyVisibility.TESTIMONIAL);
        return s;
    }
}
