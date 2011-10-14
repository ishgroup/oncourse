package ish.oncourse.portal.services.site;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;

public class PortalSiteService implements IWebSiteService {

	@Inject
	private IAuthenticationService authService;

	@Override
	public WebSite getCurrentWebSite() {
		if (getCurrentCollege() == null) {
			return null;
		} else {
			List<WebSite> webSites = getCurrentCollege().getWebSites();
			return (webSites.size() > 0) ? webSites.get(0) : null;
		}
	}

	@Override
	public College getCurrentCollege() {
		return authService.getUser().getCollege();
	}

	@Override
	public WebHostName getCurrentDomain() {
		return (getCurrentWebSite() != null) ? getCurrentWebSite().getToWebHostName() : null;
	}
}
