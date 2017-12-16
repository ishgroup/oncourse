package ish.oncourse.portal.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectCollege {

    private static final Logger logger = LogManager.getLogger();

	@Property
	@Persist(value = PersistenceConstants.CLIENT)
	private List<String> users;

	@Component
	private Form collegeForm;

	@Property
	private String user;

	@Property
	private String selectedUser;

	@Inject
	private ICookiesService cookieService;

	@InjectPage
	private Index index;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IAuthenticationService authService;

    @Inject
    private Messages messages;

	@InjectPage
	private ForgotPassword forgotPassword;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	@Persist(value = PersistenceConstants.CLIENT)
	private ArrayList<String> collegesWithDuplicates;

	@Persist(value = PersistenceConstants.CLIENT)
	private boolean passwordRecover;

    @InjectPage
    private Login login;

	public void setPasswordRecover(boolean passwordRecover) {
		this.passwordRecover = passwordRecover;
	}

	public void setTheUsers(List<Contact> contacts, Set<College> collegesWithDuplicates) {
		this.users = new ArrayList<>(5);
		this.collegesWithDuplicates = new ArrayList<>();

		for (Contact c : contacts) {
			if (collegesWithDuplicates.size() > 0) {
				// if we have 2 or more contact with the same college add only one
				if (!collegesWithDuplicates.contains(c.getCollege())){
					users.add(String.valueOf(c.getId()));
				} else if (collegesWithDuplicates.contains(c.getCollege()) && !this.collegesWithDuplicates.contains(String.valueOf(c.getCollege().getId()))) {
					users.add(String.valueOf(c.getId()));
					this.collegesWithDuplicates.add(String.valueOf(c.getCollege().getId()));
				}
			} else {
				users.add(String.valueOf(c.getId()));
			}
		}
	}
	
	@SetupRender
	Object setupRender() {
		if(users == null) {
			return pageNotFound;
		}
		this.selectedUser = users.get(0);
		return null;
	}

	@OnEvent(component = "collegeForm", value = "success")
	Object submitted() {
		collegeForm.clearErrors();
		Contact c = Cayenne.objectForPK(cayenneService.newContext(), Contact.class, selectedUser);

		if (collegesWithDuplicates.contains(String.valueOf(c.getCollege().getId()))) {
			collegeForm
					.recordError(messages.get("message-unableToLoginDuplicateContacts"));
			return null;
		}

		if (passwordRecover) {
			forgotPassword.setUser(c.getId());
			return forgotPassword;
		} else {
			authService.storeCurrentUser(c);
			URL prevPage = cookieService.popPreviousPageURL();
			return (prevPage != null) ? prevPage : index;
		}
	}

	public String getCollegeName() {
		Contact c = Cayenne.objectForPK(cayenneService.newContext(), Contact.class, user);
		return c.getCollege().getName();
	}

    /**
     * The method has been introduced to redirect users to login page when session expired
     */
    public Object onException(Throwable cause){
        if (collegesWithDuplicates == null || users == null) {
            logger.warn("Persist properties have been cleared.", cause);
        } else {
            throw new IllegalArgumentException(cause);
        }
        return login;
    }
}
