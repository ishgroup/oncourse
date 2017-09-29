package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.model.SupportPassword;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ish.oncourse.portal.services.PortalUtils.COOKIE_NAME_lastLoginTime;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public class AuthenticationService implements IAuthenticationService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ISessionManager sessionManager;
	
	@Inject
	private Request request;

	@Inject
	private ICookiesService cookieService;

	private static final String SESSION_TOKEN = "PORTAL_SESSION";
	private static final String TOKEN_DELIMITER = "&";
	private static final String TOKEN_PATTERN = "%s&%s";

	private static final int SESSION_ID_MAX_AGE = 14400;

	/**
	 * @see IAuthenticationService#authenticate(String, String, String, String)
	 */
	public List<Contact> authenticate(String firstName, String lastName, String email, String password) {
		if (trimToNull(firstName) == null ||
				trimToNull(lastName) == null ||
				trimToNull(email) == null ||
				trimToNull(password) == null) {
			return Collections.emptyList();
		}

		return ObjectSelect.query(Contact.class).where(Contact.EMAIL_ADDRESS.eq(email))
				.and(Contact.PASSWORD.eq(password))
				.and(Contact.GIVEN_NAME.eq(firstName))
				.and(Contact.FAMILY_NAME.eq(lastName)).select(cayenneService.sharedContext());
	}

	@Override
	public boolean authenticate(String supportLogin) {

		if (trimToNull(supportLogin) == null) {
			return false;
		}

		ObjectContext context = cayenneService.newNonReplicatingContext();

		SupportPassword supportPassword = ObjectSelect.query(SupportPassword.class).where(SupportPassword.PASSWORD.eq(supportLogin))
				.and(SupportPassword.EXPIRES_ON.gt(new Date()))
				.orderBy(SupportPassword.CREATED_ON.desc()).selectFirst(context);

		if (supportPassword != null) {
			Contact contact = cayenneService.sharedContext().localObject(supportPassword.getContact());
			String contactId = contact.getId().toString();
			cookieService.writeCookieValue(SESSION_TOKEN, String.format(TOKEN_PATTERN, contactId, sessionManager.createContactSession(contactId)) , SESSION_ID_MAX_AGE);
			return true;
		} else {
			return true;
		}
	}

	/**
	 * @see IAuthenticationService#authenticate(String, String, String, String)
	 */
	public List<Contact> authenticateCompany(String companyName, String email, String password) {

		if (trimToNull(companyName) == null || trimToNull(email) == null
				|| trimToNull(password) == null) {
			return Collections.emptyList();
		}

		return ObjectSelect.query(Contact.class).where(Contact.EMAIL_ADDRESS.eq(email))
				.and(Contact.PASSWORD.eq(password))
				.and(Contact.IS_COMPANY.eq(Boolean.TRUE))
				.and(Contact.FAMILY_NAME.eq(companyName)).select(cayenneService.sharedContext());
	}

	/**
	 * @see IAuthenticationService#findForPasswordRecovery(String, String, String)
	 */
	@Override
	public List<Contact> findForPasswordRecovery(String firstName, String lastName, String email) {

		if (trimToNull(firstName) == null ||
				trimToNull(lastName) == null ||
				trimToNull(email) == null) {
			return Collections.emptyList();
		}

		return ObjectSelect.query(Contact.class).where(Contact.EMAIL_ADDRESS.eq(email))
				.and(Contact.GIVEN_NAME.eq(firstName))
				.and(Contact.FAMILY_NAME.eq(lastName)).select(cayenneService.sharedContext());
	}

	@Override
	public List<Contact> findCompanyForPasswordRecovery(String companyName, String email) {

		if (trimToNull(companyName) == null ||
				trimToNull(email) == null) {
			return Collections.emptyList();
		}

		return ObjectSelect.query(Contact.class).where(Contact.EMAIL_ADDRESS.eq(email))
				.and(Contact.IS_COMPANY.eq(Boolean.TRUE))
				.and(Contact.FAMILY_NAME.eq(companyName)).select(cayenneService.sharedContext());
	}

	/**
	 * @see IAuthenticationService#findByPasswordRecoveryKey(String)
	 */
	@Override
	public Contact findByPasswordRecoveryKey(String recoveryKey) {
		return ObjectSelect.query(Contact.class).where(Contact.PASSWORD_RECOVERY_KEY.eq(recoveryKey))
				.and(Contact.PASSWORD_RECOVER_EXPIRE.gte(new Date())).selectFirst(cayenneService.sharedContext());
	}

	/**
	 * @see IAuthenticationService#getUser()
	 */
	public Contact getUser() {
		String token = cookieService.getCookieValue(SESSION_TOKEN);
		if (StringUtils.trimToNull(token) == null) {
			return null;
		}
		
		String[] nodePath = token.split(TOKEN_DELIMITER);
		if (nodePath.length != 2) {
			return null;
		}


		if (sessionManager.validSession(nodePath[0], nodePath[1])) {
			return SelectById.query(Contact.class,  Long.valueOf(nodePath[0])).selectOne(cayenneService.sharedContext());
		} else {
			return null;
		}
	}


	public Contact getSelectedUser() {
		Contact authenticatedContact = getUser();
		if (authenticatedContact == null) {
			return null;
		}
		String token = cookieService.getCookieValue(SESSION_TOKEN);
		String[] nodePath = token.split(TOKEN_DELIMITER);
		
		Long childId =  sessionManager.getSelectedChildId(nodePath[0], nodePath[1]);
		
		if (childId == null) {
			return authenticatedContact;
		} else {
			return SelectById.query(Contact.class, childId).selectOne(cayenneService.sharedContext());
		}
		
	}

	public void selectUser(Contact contact) {
		Contact authenticatedContact = getUser();
		if (authenticatedContact == null) {
			throw new IllegalArgumentException("Authenticated user missed");
		}
		String token = cookieService.getCookieValue(SESSION_TOKEN);
		String[] nodePath = token.split(TOKEN_DELIMITER);

		sessionManager.selectChild(nodePath[1], nodePath[0], contact.getId().toString());
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

		cookieService.writeCookieValue(COOKIE_NAME_lastLoginTime, user.getLastLoginTime() != null ?
				user.getLastLoginTime().toString() : new Date(0L).toString());

		ObjectContext context = cayenneService.newContext();

		Contact localUser = context.localObject(user);

		localUser.setLastLoginTime(new Date());
		context.commitChanges();
		String contactId = user.getId().toString();
		cookieService.writeCookieValue(SESSION_TOKEN, String.format(TOKEN_PATTERN, contactId, sessionManager.createContactSession(contactId)), SESSION_ID_MAX_AGE);
	}

	/**
	 * @see IAuthenticationService#logout()
	 */
	public void logout() {
		cookieService.removeCookieValue(SESSION_TOKEN);
		Session session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}
	}
}
