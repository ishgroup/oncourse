package ish.oncourse.ui.pages;

import ish.oncourse.services.site.IWebSiteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageNotFound {
	
	private static final Logger logger = LogManager.getLogger();

	@Inject
	private Request request;

	@Inject
	private IWebSiteService siteService;

	@SetupRender
	void beforeRender() {
		if (siteService.getCurrentWebSite() == null) {
			logger.info("Can't determine college domain for server name: {}", request.getServerName());
		}
	}
}
