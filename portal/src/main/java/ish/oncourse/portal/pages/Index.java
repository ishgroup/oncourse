package ish.oncourse.portal.pages;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Index {

	@InjectPage
	private Timetable timetable;

	@Inject
	private IAuthenticationService authenticationService;

	Object onActivate() {
		return timetable;
	}
}
