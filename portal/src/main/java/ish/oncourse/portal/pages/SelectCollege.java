package ish.oncourse.portal.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class SelectCollege {

	@Property
	@Persist
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

	@InjectPage
	private ForgotPassword forgotPassword;

	@Persist
	private ArrayList<String> collegesWithDuplicates;

	private boolean passwordRecover;

	public void setPasswordRecover(boolean passwordRecover) {
		this.passwordRecover = passwordRecover;
	}

	public void setTheUsers(List<Contact> contacts, Set<College> collegesWithDuplicates) {
		this.users = new ArrayList<String>(5);
		this.collegesWithDuplicates = new ArrayList<String>();

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
	void setupRender() {
		this.selectedUser = users.get(0);
	}

	@OnEvent(component = "collegeForm", value = "success")
	Object submitted() {
		collegeForm.clearErrors();
		Contact c = Cayenne.objectForPK(cayenneService.sharedContext(), Contact.class, selectedUser);

		if (collegesWithDuplicates.contains(String.valueOf(c.getCollege().getId()))) {
			collegeForm
					.recordError("You are unable to log into this site with this set of credentials. Please contact the college and let them know that there are two contacts with identical login details. If they merge those contacts, the problem will be resolved.");
			return this;
		}

		if (passwordRecover) {
			forgotPassword.setUser(c);
			return forgotPassword;
		} else {
			authService.storeCurrentUser(c);
			URL prevPage = cookieService.popPreviousPageURL();
			return (prevPage != null) ? prevPage : getTimetablePage();
		}
	}

	public String getCollegeName() {
		Contact c = Cayenne.objectForPK(cayenneService.sharedContext(), Contact.class, user);
		return c.getCollege().getName();
	}

	private Object getTimetablePage() {
		return index;
	}

}
