package ish.oncourse.ui.components;

import ish.oncourse.services.site.IWebSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

public class GoogleTagmanager {
	@Inject
	private IWebSiteService siteService;

	public String getAccount() {
		String account = siteService.getCurrentWebSite().getGoogleTagmanagerAccount();
		return (StringUtils.trimToNull(account) == null) ? null : account.trim();
	}
}
