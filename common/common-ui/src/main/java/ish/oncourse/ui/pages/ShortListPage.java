package ish.oncourse.ui.pages;

import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ShortListPage {

	@Inject
	@Property
	private Request request;
	
	@Inject
	private IWebSiteService siteService;
	@Property
	private College college;

	@SetupRender
	void beforeRender() {
		college = siteService.getCurrentCollege();
	}

}
