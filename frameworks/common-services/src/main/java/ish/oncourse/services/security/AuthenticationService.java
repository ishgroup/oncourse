package ish.oncourse.services.security;

import ish.oncourse.model.College;
import ish.oncourse.model.WillowUser;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class AuthenticationService implements IAuthenticationService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService siteService;
	
	@Inject
	private Request request;
	
	@Inject
	private Cookies cookies;

	// 'applicationStateManager' is needed to look up user objects as session
	// state can not be injected in the services
	@Inject
	private ApplicationStateManager applicationStateManager;

	public AuthenticationStatus authenticate(String userName, String password) {

		if ((userName == null || userName.length() == 0)
				|| (password == null || password.length() == 0)) {
			return AuthenticationStatus.INVALID_CREDENTIALS;
		}

		College college = siteService.getCurrentCollege();

		SelectQuery query = new SelectQuery(WillowUser.class);

		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.COLLEGE_PROPERTY, college));
		query.orQualifier(ExpressionFactory.matchExp(
				WillowUser.COLLEGE_PROPERTY, null));

		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.EMAIL_PROPERTY, userName));
		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.PASSWORD_PROPERTY, password));

		@SuppressWarnings("unchecked")
		List<WillowUser> users = cayenneService.newContext()
				.performQuery(query);

		AuthenticationStatus status = AuthenticationStatus.NO_MATCHING_USER;

		if ((users != null) && (users.size() == 1)) {
			applicationStateManager.set(WillowUser.class, users.get(0));
			status = AuthenticationStatus.SUCCESS;
		} else if ((users != null) && (users.size() > 1)) {
			status = AuthenticationStatus.MORE_THAN_ONE_USER;
		}

		return status;
	}

	public WillowUser getUser() {
		// TODO: andrus, 20.10.2009: check if the user belongs to the current
		// college??
		return applicationStateManager.getIfExists(WillowUser.class);
	}

	public void logout() {
		Session session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		
		cookies.removeCookieValue("cms_session");
	}
}
