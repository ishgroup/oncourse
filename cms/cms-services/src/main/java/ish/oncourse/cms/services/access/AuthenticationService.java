package ish.oncourse.cms.services.access;

import ish.oncourse.model.College;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.WillowUser;
import ish.oncourse.services.access.AuthenticationStatus;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.util.SecurityUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AuthenticationStatus succedAuthentication(Class ssoClass, Object SSO) {
		applicationStateManager.set(ssoClass, SSO);
		cookiesService.writeCookieValue("cms", "enabled");
		return AuthenticationStatus.SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public AuthenticationStatus authenticate(String userName, String password) {
		if (StringUtils.trimToNull(userName) == null || StringUtils.trimToNull(password) == null) {
			return AuthenticationStatus.INVALID_CREDENTIALS;
		}
		College college = siteService.getCurrentCollege();
		String hashedPassword = null;
		try {
			hashedPassword = SecurityUtil.hashPassword(password);
		} catch (UnsupportedEncodingException e) {
			LOG.error(String.format("Failed to hash the user password for check with value %s", password), e);
			hashedPassword = StringUtils.EMPTY;
			//return AuthenticationStatus.INVALID_CREDENTIALS;
		}
		
		SelectQuery systemUserEmailQuery = new SelectQuery(SystemUser.class);
		systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.COLLEGE_PROPERTY, college));
		systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.EMAIL_PROPERTY, userName));
		systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.PASSWORD_PROPERTY, hashedPassword));
		List<SystemUser> systemUsers = cayenneService.newContext().performQuery(systemUserEmailQuery);
		
		AuthenticationStatus status = AuthenticationStatus.NO_MATCHING_USER;
		if (!systemUsers.isEmpty()) {
			if (systemUsers.size() > 1) {
				status = AuthenticationStatus.MORE_THAN_ONE_USER;
			} else {
				status = succedAuthentication(SystemUser.class, systemUsers.get(0));
			}
		} else {
			//try to login by login and password
			systemUserEmailQuery = new SelectQuery(SystemUser.class);
			systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.COLLEGE_PROPERTY, college));
			systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.LOGIN_PROPERTY, userName));
			systemUserEmailQuery.andQualifier(ExpressionFactory.matchExp(SystemUser.PASSWORD_PROPERTY, hashedPassword));
			systemUsers = cayenneService.newContext().performQuery(systemUserEmailQuery);
			if (!systemUsers.isEmpty()) {
				if (systemUsers.size() > 1) {
					status = AuthenticationStatus.MORE_THAN_ONE_USER;
				} else {
					status = succedAuthentication(SystemUser.class, systemUsers.get(0));
				}
			} else {
				//try to login with willow user
				final SelectQuery query = new SelectQuery(WillowUser.class);
				query.andQualifier(ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, college));
				query.orQualifier(ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, null));
				
				query.andQualifier(ExpressionFactory.matchExp(WillowUser.EMAIL_PROPERTY, userName));
				query.andQualifier(ExpressionFactory.matchExp(WillowUser.PASSWORD_PROPERTY, password));//TODO: setup via hashedPassword 
				
				final List<WillowUser> users = cayenneService.newContext().performQuery(query);
				if (!users.isEmpty()) {
					if (users.size() > 1) {
						status = AuthenticationStatus.MORE_THAN_ONE_USER;
					} else {
						status = succedAuthentication(WillowUser.class, users.get(0));
					}
				}
			}
		}
		return status;
	}

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

	public void logout() {
		Session session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		cookiesService.removeCookieValue("cms");
	}
}
