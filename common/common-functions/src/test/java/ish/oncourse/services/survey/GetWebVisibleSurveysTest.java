package ish.oncourse.services.survey;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.Survey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetWebVisibleSurveysTest {

    @Test
    public void testQualifier() {
        assertEquals(Survey.TESTIMONIAL.isNotNull().andExp(Survey.VISIBILITY.eq(SurveyVisibility.TESTIMONIAL)),
                ish.oncourse.services.survey.GetWebVisibleSurveys.valueOf(null).VISIBLE);
    }
}
