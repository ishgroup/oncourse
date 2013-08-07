package ish.oncourse.portal.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

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
		WebSite webSite = getCurrentWebSite();
		if (webSite != null)
		{
			WebHostName mainDomain = webSite.getToWebHostName();
			if (mainDomain == null)
			{
				//if main domain was not set we select the first domain form webSite domains list.
				List<WebHostName> webHostNames = webSite.getCollegeDomains();
				if (webHostNames.size() > 0)
					mainDomain = webHostNames.get(0);
			}
			return mainDomain;
		}
		else
			return null;
	}
}
