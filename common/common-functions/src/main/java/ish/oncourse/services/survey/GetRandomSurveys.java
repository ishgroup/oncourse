package ish.oncourse.services.survey;

import ish.oncourse.model.Survey;
import ish.oncourse.services.course.GetRandomNonRepeatableInBound;

import java.util.ArrayList;
import java.util.List;

public class GetRandomSurveys {

    private List<Survey> surveys;
    private int count;

    public static GetRandomSurveys valueOf(List<Survey> surveys, int count) {
        GetRandomSurveys getter = new GetRandomSurveys();
        getter.surveys = surveys;
        getter.count = count;
        return getter;
    }

    public List<Survey> get() {
        List<Integer> randomNonRepeatableIndexes = GetRandomNonRepeatableInBound.valueOf(surveys.size(), count).get();

        List<Survey> randomTestimonials = new ArrayList<>();
        for (Integer index : randomNonRepeatableIndexes) {
            randomTestimonials.add(surveys.get(index));
        }
        return randomTestimonials;
    }
}
