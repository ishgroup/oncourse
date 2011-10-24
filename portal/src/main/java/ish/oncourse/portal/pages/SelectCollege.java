package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class SelectCollege {

	@Property
	@Persist
	private List<String> users;
	
	@Property
	private Boolean isDuplicateInCollege; 

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

	private boolean passwordRecover;
	
	public void setPasswordRecover(boolean passwordRecover) {
		this.passwordRecover = passwordRecover;
	}

	public void setTheUsers(List<Contact> contacts) {
		this.users = new ArrayList<String>(5);
		for (Contact c : contacts) {
			users.add(String.valueOf(c.getId()));
		}
	}

	@SetupRender
	void setupRender() {
		this.selectedUser = users.get(0);
		for(int i = 0; i < users.size(); i++){
			String tempUser = users.get(i);
			Contact c = Cayenne.objectForPK(cayenneService.sharedContext(), Contact.class, tempUser);
			
			for (int j = 0; j < users.size(); j++){
				String tmpUser = users.get(j);
				if(tmpUser != tempUser){
					Contact contact = Cayenne.objectForPK(cayenneService.sharedContext(), Contact.class, tmpUser);
					if(contact.getCollege().equals(c.getCollege())){
						this.isDuplicateInCollege= true;
					}
				}
			}
		}
	}

	@OnEvent(component = "collegeForm", value = "success")
	Object submitted() {
		Contact c = Cayenne.objectForPK(cayenneService.sharedContext(), Contact.class, selectedUser);
		
		if (passwordRecover) {
			forgotPassword.setUser(c);
			return forgotPassword;
		}
		else {
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
