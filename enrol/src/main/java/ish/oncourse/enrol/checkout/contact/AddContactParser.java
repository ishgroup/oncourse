package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

public class AddContactParser {

	public static final String FIELD_NAME_firstName = "firstName";
	public static final String FIELD_NAME_lastName = "lastName";
	public static final String FIELD_NAME_email = "email";

	private Request request;
	private ContactCredentials contactCredentials;

	private Map<String, String> errors = new HashMap<>();

	public void parse()
	{
		contactCredentials.setFirstName(StringUtils.trimToNull(request.getParameter(FIELD_NAME_firstName)));
		contactCredentials.setLastName(StringUtils.trimToNull(request.getParameter(FIELD_NAME_lastName)));
		contactCredentials.setEmail(StringUtils.trimToNull(request.getParameter(FIELD_NAME_email)));
		parseFirstName();
		parseLastName();
		parseEmail();
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



}
