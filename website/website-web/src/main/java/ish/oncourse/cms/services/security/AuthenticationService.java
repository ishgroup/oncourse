package ish.oncourse.cms.services.security;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;

import ish.oncourse.model.College;
import ish.oncourse.model.WillowUser;
import ish.oncourse.services.college.ICollegeService;
import ish.oncourse.services.persistence.ICayenneService;

public class AuthenticationService implements IAuthenticationService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	@Inject
	// 'applicationStateManager' is needed to look up user objects as session
	// state can not be injected in the services
	private ApplicationStateManager applicationStateManager;

	public AutenticationStatus authenticate(String userName, String password) {

		if (userName == null || userName.length() == 0) {
			return AutenticationStatus.EMPTY_USER_NAME;
		}

		if (password == null || password.length() == 0) {
			return AutenticationStatus.EMPTY_PASSWORD;
		}

		College college = collegeService.getCurrentCollege();

		SelectQuery query = new SelectQuery(WillowUser.class);

		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.COLLEGE_PROPERTY, college));
		query.orQualifier(ExpressionFactory.matchExp(
				WillowUser.COLLEGE_PROPERTY, null));

		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.EMAIL_PROPERTY, userName));
		query.andQualifier(ExpressionFactory.matchExp(
				WillowUser.PASSWORD_PROPERTY, password));

		List<WillowUser> users = cayenneService.newContext()
				.performQuery(query);

		if (users.isEmpty()) {
			return AutenticationStatus.NO_MATCHING_USER;
		}

		if (users.size() > 1) {
			return AutenticationStatus.MORE_THAN_ONE_USER;
		}

		applicationStateManager.set(WillowUser.class, users.get(0));

		return AutenticationStatus.SUCCESS;
	}

	public WillowUser getUser() {

		// TODO: andrus, 20.10.2009: check if the user belongs to the current
		// college??
		return applicationStateManager.getIfExists(WillowUser.class);
	}
}
