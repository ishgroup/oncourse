package ish.oncourse.portal.pages;

import org.apache.tapestry5.annotations.InjectPage;

public class Index {

	@InjectPage
	private Timetable timetable;

	Object onActivate() {
		return timetable;
	}
}
