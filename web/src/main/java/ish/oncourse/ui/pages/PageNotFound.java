package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageNotFound extends ISHCommon {
	
	private static final Logger logger = LogManager.getLogger();

	@Inject
	private IWebSiteService siteService;

	@SetupRender
	void beforeRender() {
		if (siteService.getCurrentWebSite() == null) {
			logger.info("Can't determine college domain for server name: {}", request.getServerName());
		}
	}
}
