package ish.oncourse.ui.utils;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.tutor.GetVisibleTutorRoles;
import ish.oncourse.services.tutor.GetIsActiveTutor;

import java.util.List;
import java.util.stream.Collectors;

public class GetVisibleTutors {

    private CourseClass courseClass;

    public List<Tutor> get() {
        return getRoles().stream()
                .filter(role -> GetIsActiveTutor.valueOf(role.getTutor()).get())
                .map(role -> role.getTutor())
                .collect(Collectors.toList());
    }

    public static GetVisibleTutors valueOf(CourseClass courseClass) {
        GetVisibleTutors result = new GetVisibleTutors();
        result.courseClass = courseClass;
        return result;
    }

    protected List<TutorRole> getRoles() {
        return GetVisibleTutorRoles.valueOf(courseClass.getObjectContext(), courseClass).get();
    }
}
