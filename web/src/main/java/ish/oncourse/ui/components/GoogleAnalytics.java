package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.ioc.annotations.Inject;


/**
 *
 * @author marek
 */
@Deprecated
public class GoogleAnalytics extends ISHCommon {

	@Inject
	private IWebSiteService siteService;

	public String getAnalyticsAccount() {
		return null;
	}

	public boolean isAccountValid() {
		return false;
	}
}
