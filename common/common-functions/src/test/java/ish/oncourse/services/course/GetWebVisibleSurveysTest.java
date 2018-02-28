package ish.oncourse.services.course;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.Survey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetWebVisibleSurveysTest {

    @Test
    public void test() {
        assertEquals(Survey.TESTIMONIAL.isNotNull().andExp(Survey.VISIBILITY.eq(SurveyVisibility.TESTIMONIAL)),
                GetWebVisibleSurveys.valueOf(null).VISIBLE);
    }
}
