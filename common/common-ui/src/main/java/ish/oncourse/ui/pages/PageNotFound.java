package ish.oncourse.ui.pages;

import org.apache.log4j.Logger;

import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageNotFound {
	
	private static final Logger logger = Logger.getLogger(PageNotFound.class);

	@Inject
	private Request request;

	@Inject
	private IWebSiteService siteService;

	@SetupRender
	void beforeRender() {
		if (siteService.getCurrentWebSite() == null) {
			logger.info("Can't determine college domain for server name: '" + request.getServerName() + "'");
		}
	}
}
