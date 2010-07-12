package ish.oncourse.ui.components;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.site.IWebSiteService;


public class BodyHeader {

	@Inject
	private IWebSiteService siteService;


	public String getCollegeName() {
		return siteService.getCurrentCollege().getName();
	}

	public String getHomeLink() {
		return siteService.getHomeLink();
	}
}
