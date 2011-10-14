package ish.oncourse.portal.pages.tutor;

import org.apache.tapestry5.annotations.InjectPage;

import ish.oncourse.portal.annotations.UserRole;

@UserRole("tutor")
public class Profile extends ish.oncourse.portal.pages.student.Profile {

	@InjectPage("tutor/timetable")
	private Object timetableTutor;

	@Override
	protected Object getTimetablePage() {
		return timetableTutor;
	}
}
