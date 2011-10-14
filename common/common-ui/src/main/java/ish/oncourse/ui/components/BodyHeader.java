package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;

public class BodyHeader {

	@Inject
	private IWebSiteService siteService;
	@Property
	private College college;

	@SetupRender
	void beforeRender() {
		college = siteService.getCurrentCollege();
	}

	public String getCollegeName() {
		return college.getName();
	}

	public String getHomeLink() {
		return "/";
	}
}
