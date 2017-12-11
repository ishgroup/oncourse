package ish.oncourse.ui.utils;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.tutor.ITutorService;

import java.util.ArrayList;
import java.util.List;

public class GetVisibleTutors {

    private CourseClass courseClass;

    private ITutorService tutorService;

    private List<TutorRole> tutorRoles = new ArrayList<>();
    private List<Tutor> tutors = new ArrayList<>();
    private List<String> tutorNames = new ArrayList<>();


    public GetVisibleTutors get() {
        for (TutorRole role : courseClass.getTutorRoles()) {
            if (role.getInPublicity() && tutorService.isActiveTutor(role.getTutor())) {
                tutorRoles.add(role);
                tutors.add(role.getTutor());
                tutorNames.add(getTutorName(role.getTutor()));
            }
        }
        return this;
    }

    public List<TutorRole> getTutorRoles() {
        return tutorRoles;
    }

    public List<Tutor> getTutors() {
        return tutors;
    }

    public List<String> tutorNames() {
        return tutorNames;
    }

    public static String getTutorName(Tutor tutor) {
        Contact contact = tutor.getContact();
        if (Boolean.TRUE.equals(contact.getIsCompany())) {
            return contact.getFamilyName();
        } else {
            return String.format("%s %s", contact.getGivenName(), contact.getFamilyName());
        }
    }

    public static GetVisibleTutors valueOf(CourseClass courseClass, ITutorService tutorService) {
        GetVisibleTutors result = new GetVisibleTutors();
        result.courseClass = courseClass;
        result.tutorService = tutorService;
        return result;
    }
}
