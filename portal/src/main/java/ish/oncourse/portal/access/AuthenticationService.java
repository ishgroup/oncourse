package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class AuthenticationService implements IAuthenticationService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ApplicationStateManager applicationStateManager;

	@Inject
	private Request request;

    @Inject
    private ICookiesService cookieService;

	/**
	 * @see IAuthenticationService#authenticate(String, String, String, String)
	 */
	public List<Contact> authenticate(String firstName, String lastName, String email, String password) {

		if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty()
				|| password == null || password.isEmpty()) {
			return Collections.emptyList();
		}

		SelectQuery query = new SelectQuery(Contact.class);

		query.andQualifier(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, email));
		query.andQualifier(ExpressionFactory.matchExp(Contact.PASSWORD_PROPERTY, password));
		query.andQualifier(ExpressionFactory.matchExp(Contact.GIVEN_NAME_PROPERTY, firstName));
		query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, lastName));

		@SuppressWarnings("unchecked")
		List<Contact> users = cayenneService.newContext().performQuery(query);
		return users;
	}
	
	/**
	 * @see IAuthenticationService#authenticate(String, String, String, String)
	 */
	public List<Contact> authenticateCompany(String companyName, String email, String password) {

		if (companyName == null || companyName.isEmpty() || email == null || email.isEmpty()
				|| password == null || password.isEmpty()) {
			return Collections.emptyList();
		}

		SelectQuery query = new SelectQuery(Contact.class);

		query.andQualifier(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, email));
		query.andQualifier(ExpressionFactory.matchExp(Contact.PASSWORD_PROPERTY, password));
		query.andQualifier(ExpressionFactory.matchExp(Contact.IS_COMPANY_PROPERTY, Boolean.TRUE));
		query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, companyName));

		@SuppressWarnings("unchecked")
		List<Contact> users = cayenneService.newContext().performQuery(query);
		return users;
	}

	/**
	 * @see IAuthenticationService#findForPasswordRecovery(String, String, String)
	 */
	@Override
	public List<Contact> findForPasswordRecovery(String firstName, String lastName, String email) {

		if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty()) {
			return Collections.emptyList();
		}

		SelectQuery query = new SelectQuery(Contact.class);

		query.andQualifier(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, email));
		query.andQualifier(ExpressionFactory.matchExp(Contact.GIVEN_NAME_PROPERTY, firstName));
		query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, lastName));

		@SuppressWarnings("unchecked")
		List<Contact> users = cayenneService.newContext().performQuery(query);
		return users;
	}
	
	@Override
	public List<Contact> findCompanyForPasswordRecovery(String companyName, String email) {
		
		if (companyName == null || companyName.isEmpty() || email == null || email.isEmpty()) {
			return Collections.emptyList();
		}

		SelectQuery query = new SelectQuery(Contact.class);

		query.andQualifier(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, email));
		query.andQualifier(ExpressionFactory.matchExp(Contact.IS_COMPANY_PROPERTY, Boolean.TRUE));
		query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, companyName));

		@SuppressWarnings("unchecked")
		List<Contact> users = cayenneService.newContext().performQuery(query);
		return users;
	}

	/**
	 * @see IAuthenticationService#findByPasswordRecoveryKey(String)
	 */
	@Override
	public Contact findByPasswordRecoveryKey(String recoveryKey) {
		Date today = new Date();
		Expression expr = ExpressionFactory.matchExp(Contact.PASSWORD_RECOVERY_KEY_PROPERTY, recoveryKey);
		expr = expr.andExp(ExpressionFactory.greaterOrEqualExp(Contact.PASSWORD_RECOVER_EXPIRE_PROPERTY, today));
		SelectQuery query = new SelectQuery(Contact.class, expr);
		@SuppressWarnings("unchecked")
		List<Contact> users = cayenneService.sharedContext().performQuery(query);
		return (users.isEmpty()) ? null : users.get(0);
	}

	/**
	 * @see IAuthenticationService#getUser()
	 */
	public Contact getUser() {
		return applicationStateManager.getIfExists(Contact.class);
	}

	/**
	 * @see IAuthenticationService#isTutor()
	 */
	@Override
	public boolean isTutor() {
		return getUser() != null && getUser().getTutor() != null;
	}

	/**
	 * @see IAuthenticationService#storeCurrentUser(Contact)
	 */
	@Override
	public void storeCurrentUser(Contact user) {

            cookieService.writeCookieValue("lastLoginTime", user.getLastLoginTime() != null ?
                    user.getLastLoginTime().toString() : new Date(0l).toString());

        ObjectContext context = cayenneService.newContext();

        Contact localUser = context.localObject(user);

        localUser.setLastLoginTime(new Date());
        context.commitChanges();

		applicationStateManager.set(Contact.class, user);
	}

	/**
	 * @see IAuthenticationService#logout()
	 */
	public void logout() {

		Session session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}
	}

	
}
