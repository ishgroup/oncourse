package ish.oncourse.services.course;

import ish.oncourse.model.Survey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

public class GetRandomSurveysTest {

    @Test
    public void test() {
        List<Survey> surveys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            surveys.add(mock(Survey.class));
        }

        assertEquals(5, GetRandomSurveys.valueOf(surveys, 5).get().size());
        assertEquals(0, GetRandomSurveys.valueOf(surveys, 0).get().size());
        assertEquals(0, GetRandomSurveys.valueOf(surveys, -1).get().size());
    }
}
