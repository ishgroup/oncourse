package ish.oncourse.portal.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.List;
import java.util.TimeZone;

public class PortalSiteService implements IWebSiteService {

    @Inject
    private Request request;

	@Inject
	private IAuthenticationService authService;

    @Inject
    private ICollegeService collegeService;

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
        if (authService.getUser() == null) {
            // we need the code for usi page.
            Long collegeId = (Long) request.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE);
            return collegeService.findById(collegeId);
        }
        else {
            return authService.getUser().getCollege();
        }
	}

	@Override
	public TimeZone getTimezone() {
		return TimeZone.getTimeZone(this.getCurrentCollege().getTimeZone());
	}

	@Override
	public List<WebSite> getSiteTemplates() {
		throw new UnsupportedOperationException();
	}
}
