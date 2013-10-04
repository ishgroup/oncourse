package ish.oncourse.cms.services.access;

import ish.oncourse.model.College;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.WillowUser;
import ish.oncourse.services.access.AuthenticationStatus;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.security.AuthenticationUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.List;

public class AuthenticationService implements IAuthenticationService {
	private static final Logger LOG = Logger.getLogger(AuthenticationService.class);
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private Request request;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ApplicationStateManager applicationStateManager;

	private AuthenticationStatus succedAuthentication(Class ssoClass, Object SSO) {
		applicationStateManager.set(ssoClass, SSO);
		cookiesService.writeCookieValue("cms", "enabled");
		return AuthenticationStatus.SUCCESS;
	}

	@Override
	public AuthenticationStatus authenticate(String userName, String password) {
		if (StringUtils.trimToNull(userName) == null || StringUtils.trimToNull(password) == null) {
			return AuthenticationStatus.INVALID_CREDENTIALS;
		}

		AuthenticationStatus status = authenticateByEmail(userName, password);

		if (AuthenticationStatus.NO_MATCHING_USER.equals(status)) {
			status = authenticateByLogin(userName, password);
		}

		return status;
	}

	private AuthenticationStatus authenticateByEmail(String email, String password) {

		College college = siteService.getCurrentCollege();

		SelectQuery systemUserEmailQuery = new SelectQuery(SystemUser.class);
		systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.COLLEGE_PROPERTY, college));
		systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.EMAIL_PROPERTY, email));
		List<SystemUser> systemUsers = cayenneService.newContext().performQuery(systemUserEmailQuery);

		if (systemUsers.isEmpty()) {
			return AuthenticationStatus.NO_MATCHING_USER;
		}

		if (systemUsers.size() > 1) {
			return AuthenticationStatus.MORE_THAN_ONE_USER;
		}

		SystemUser user = systemUsers.get(0);

		if (tryAuthenticate(user, password)) {
			return succedAuthentication(SystemUser.class, user);
		} else {
			return AuthenticationStatus.INVALID_CREDENTIALS;
		}
	}

	private AuthenticationStatus authenticateByLogin(String login, String password) {

		College college = siteService.getCurrentCollege();

		SelectQuery systemUserLoginQuery = new SelectQuery(SystemUser.class);
		systemUserLoginQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.COLLEGE_PROPERTY, college));
		systemUserLoginQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.LOGIN_PROPERTY, login));
		List<SystemUser> systemUsers = cayenneService.newContext().performQuery(systemUserLoginQuery);

		if (systemUsers.isEmpty()) {
			return AuthenticationStatus.NO_MATCHING_USER;
		}

		if (systemUsers.size() > 1) {
			return AuthenticationStatus.MORE_THAN_ONE_USER;
		}

		SystemUser user = systemUsers.get(0);

		if (tryAuthenticate(user, password)) {
			return succedAuthentication(SystemUser.class, user);
		} else {
			return AuthenticationStatus.INVALID_CREDENTIALS;
		}
	}

	private boolean tryAuthenticate(SystemUser user, String password) {
		if (AuthenticationUtil.isValidPasswordHash(user.getPassword())) {
			// normal authenticatioin procedure
			return AuthenticationUtil.checkPassword(password, user.getPassword());
		} else {
			// fallback to old password hashing system
			if (AuthenticationUtil.checkOldPassword(password, user.getPassword())) {

				// if password is correct then replace old hash with new one
				ObjectContext context = cayenneService.newContext();

				SystemUser localUser = context.localObject(user);

				String newHash = AuthenticationUtil.generatePasswordHash(password);
				localUser.setPassword(newHash);

				context.commitChanges();

				return true;
			}
		}

		return false;
	}

	@Override
	public WillowUser getUser() {
		WillowUser user = applicationStateManager.getIfExists(WillowUser.class);
		if (user != null) {
			boolean belongToCollege = (user.getCollege() == null) || 
				(user.getCollege() != null && siteService.getCurrentCollege().getId().equals(user.getCollege().getId()));
			if (belongToCollege) {
				return user;
			}
		}
		return null;
	}

	@Override
	public SystemUser getSystemUser() {
		SystemUser user = applicationStateManager.getIfExists(SystemUser.class);
		if (user != null) {
			boolean belongToCollege = user.getCollege().getId().equals(siteService.getCurrentCollege().getId());
			if (belongToCollege) {
				return user;
			}
		}
		return null;
	}

	@Override
	public void logout() {
		Session session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		cookiesService.removeCookieValue("cms");
	}
}
