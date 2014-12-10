package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.utils.StringUtilities;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

public class AddContactParser {

	public static final String FIELD_NAME_firstName = "firstName";
	public static final String FIELD_NAME_lastName = "lastName";
	public static final String FIELD_NAME_email = "email";

	private Request request;
	private ContactCredentials contactCredentials;
	private boolean isCompany = false;

	private Map<String, String> errors = new HashMap<>();

	public void parse()
	{
		contactCredentials.setFirstName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_firstName)));
		contactCredentials.setLastName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_lastName)));
		contactCredentials.setEmail(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_email)));
		parseLastName();
		parseEmail();
		
		if (!isCompany) {
			parseFirstName();
		}
	}

	private void parseFirstName()
	{
		String message = Contact.validateGivenName("student", contactCredentials.getFirstName());
		if (message != null)
			errors.put(FIELD_NAME_firstName, message);
	}

	private void parseLastName()
	{
		String message = Contact.validateFamilyName("student", contactCredentials.getLastName());
		if (message != null)
			errors.put(FIELD_NAME_lastName, message);
	}

	private void parseEmail()
	{
		String message = Contact.validateEmail("student", contactCredentials.getEmail());
		if (message != null)
			errors.put(FIELD_NAME_email, message);
	}


	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

	public void setContactCredentials(ContactCredentials contactCredentials) {
		this.contactCredentials = contactCredentials;
	}

	public Map<String, String> getErrors() {
		return errors;
	}


	public void setCompany(boolean isCompany) {
		this.isCompany = isCompany;
	}
}
