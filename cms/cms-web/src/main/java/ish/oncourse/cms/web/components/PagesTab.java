package ish.oncourse.cms.web.components;

import org.apache.tapestry5.annotations.InjectPage;

import ish.oncourse.cms.web.pages.NewPage;
import org.apache.tapestry5.annotations.Property;

public class PagesTab {
	
	@InjectPage
	@Property
	private NewPage next;
	
	Object onSuccess() {
		return next;
	}
}
