package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.access.SessionToken;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.List;
import java.util.TimeZone;

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
		GetAttribute<SessionToken> tokenA = new GetAttribute<>(session, request);
		GetAttribute<Long> collegeA = new GetAttribute<>(session, request);
		if (tokenA.get(SessionToken.SESSION_TOKEN_KEY) != null)
			collegeId = tokenA.get(SessionToken.SESSION_TOKEN_KEY).getCollegeId();
		else if (collegeA.get(College.REQUESTING_COLLEGE_ATTRIBUTE) != null)
			collegeId = collegeA.get(College.REQUESTING_COLLEGE_ATTRIBUTE);
		else
			return null;
		return collegeService.findById(collegeId);
	}

	@Override
	public TimeZone getTimezone() {
		return TimeZone.getTimeZone(this.getCurrentCollege().getTimeZone());
	}

	@Override
	public List<WebSite> getSiteTemplates() {
		throw new UnsupportedOperationException();
	}


	public class GetAttribute<T> {
		private Session session;
		private Request request;

		public GetAttribute(Session session, Request request) {
			this.session = session != null && !session.isInvalidated() ? session : null;
			this.request = request;
		}

		public T get(String key) {
			if (session != null) {
				return (T) session.getAttribute(key);
			}

			if (request != null) {
				return (T) request.getAttribute(key);
			}
			return null;
		}
	}
}
