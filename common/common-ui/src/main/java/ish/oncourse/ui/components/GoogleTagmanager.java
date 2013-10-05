package ish.oncourse.ui.components;

import ish.oncourse.services.site.IWebSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The component inits google tag mananger script and sends dataLayer object
 * if google tag manager account is set for this web site.
 */
public class GoogleTagmanager {
	@Inject
	private IWebSiteService siteService;

	/**
	 * Google tag mananger event name.
	 */
	@Property
	@Parameter
	private String eventName;

	@Property
	@Parameter
	private String cart;

	public String getAccount() {
		String account = siteService.getCurrentWebSite().getGoogleTagmanagerAccount();
		return (StringUtils.trimToNull(account) == null) ? null : account.trim();
	}
}
