package ish.oncourse.cms.components;

import org.apache.tapestry5.annotations.InjectPage;

import ish.oncourse.cms.pages.NewPage;
import org.apache.tapestry5.annotations.Property;

public class PagesTab {
	
	@InjectPage
	@Property
	private NewPage next;
	
	Object onSuccess() {
		return next;
	}
}
