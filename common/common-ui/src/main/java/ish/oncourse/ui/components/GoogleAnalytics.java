package ish.oncourse.ui.components;

import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.annotations.Inject;


/**
 *
 * @author marek
 */
public class GoogleAnalytics {

	@Inject
	private IWebSiteService siteService;

	public String getAnalyticsAccount() {
		String analyticsAccount = siteService.getCurrentWebSite().getGoogleAnalyticsAccount();

		return (analyticsAccount == null) ? "" : analyticsAccount.trim();
	}

	public boolean isAccountValid() {
		return ((getAnalyticsAccount() != null) && (! "".equals(getAnalyticsAccount())));
	}
}
