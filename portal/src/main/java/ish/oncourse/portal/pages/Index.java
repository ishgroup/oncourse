package ish.oncourse.portal.pages;

import org.apache.tapestry5.annotations.InjectPage;

public class Index {

	@InjectPage
	private Dashboard dashboard;

	Object onActivate() {
		return dashboard;
	}
}
