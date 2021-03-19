package ish.oncourse.ui.utils;

import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.tutor.GetIsActiveTutor;
import ish.oncourse.services.tutor.GetSessionVisibleTutorRoles;

import java.util.List;
import java.util.stream.Collectors;

public class GetVisibleTutors {

    private Session session;

    public List<Tutor> get() {
        return getRoles().stream()
                .filter(role -> GetIsActiveTutor.valueOf(role.getTutor()).get())
                .map(role -> role.getTutor())
                .collect(Collectors.toList());
    }

    public static GetVisibleTutors valueOf(Session session) {
        GetVisibleTutors result = new GetVisibleTutors();
        result.session = session;
        return result;
    }

    protected List<TutorRole> getRoles() {
        return GetSessionVisibleTutorRoles.valueOf(session.getObjectContext(), session).get();
    }
}
