package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.List;

public class WebSiteServiceOverride implements IWebSiteService {

	@Inject
	private Request request;

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
		Session session = request.getSession(false);
		Long collegeId = null;
		if (session != null && !session.isInvalidated()) {
			if (session.getAttribute(SessionToken.SESSION_TOKEN_KEY) != null) {
				SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
				collegeId = token.getCollegeId();
			}
			else if (session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE) != null) {
				collegeId = (Long) session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE);
			}
		} else {
			if (request.getAttribute(SessionToken.SESSION_TOKEN_KEY) != null) {
				SessionToken token = (SessionToken) request.getAttribute(SessionToken.SESSION_TOKEN_KEY);
				collegeId = token.getCollegeId();
			} else if (request.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE) != null) {
				collegeId = (Long) request.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE);
			}
		}
		if (collegeId == null) {
			return null;
		}
		
		return collegeService.findById(collegeId);
	}

	@Override
	public WebHostName getCurrentDomain() {
		return (getCurrentWebSite() != null) ? getCurrentWebSite().getToWebHostName() : null;
	}
}
