package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.tapestry5.services.Request;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestToSurveyTest {

    /**
     * test parsing and trimming data
     */
    @Test
    public void testParsingRequest() {
        Survey survey = new Survey();

        RequestToSurvey.valueOf(survey, prepareRequest()).parse();

        assertEquals((Integer) 5, survey.getTutorScore());
        assertEquals((Integer) 4, survey.getCourseScore());
        assertEquals((Integer) 3, survey.getVenueScore());
        assertEquals((Integer) 10, survey.getNetPromoterScore());
        assertEquals("test comment", survey.getComment());
    }

    /**
     * empty parameters in request should not change a existed values in survey
     * @throws Exception
     */
    @Test
    public void testEmptyRequest() throws Exception {
        Survey survey = new Survey();
        invokePostAdd(survey);

        RequestToSurvey.valueOf(survey, prepareEmptyRequest()).parse();

        assertEquals((Integer) 0, survey.getTutorScore());
        assertEquals((Integer) 0, survey.getCourseScore());
        assertEquals((Integer) 0, survey.getVenueScore());
        assertEquals((Integer) 0, survey.getNetPromoterScore());
        assertNull(survey.getComment());
    }

    private Request prepareRequest() {
        Request request = mock(Request.class);

        when(request.getParameter(Survey.TUTOR_SCORE.getName())).thenReturn("5 ");
        when(request.getParameter(Survey.COURSE_SCORE.getName())).thenReturn(" 4");
        when(request.getParameter(Survey.VENUE_SCORE.getName())).thenReturn("3\t");
        when(request.getParameter(Survey.NET_PROMOTER_SCORE.getName())).thenReturn("\n10");
        when(request.getParameter(Survey.COMMENT.getName())).thenReturn("\ttest comment\n ");
        return request;
    }

    private Request prepareEmptyRequest() {
        Request request = mock(Request.class);

        when(request.getParameter(Survey.TUTOR_SCORE.getName())).thenReturn(null);
        when(request.getParameter(Survey.COURSE_SCORE.getName())).thenReturn(null);
        when(request.getParameter(Survey.VENUE_SCORE.getName())).thenReturn(null);
        when(request.getParameter(Survey.NET_PROMOTER_SCORE.getName())).thenReturn(null);
        when(request.getParameter(Survey.COMMENT.getName())).thenReturn(null);
        return request;
    }

    private void invokePostAdd(Survey s) throws Exception {
        Class c = s.getClass();
        Method m = c.getDeclaredMethod("onPostAdd");
        m.setAccessible(true);
        m.invoke(s);
    }
}
