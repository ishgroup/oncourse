package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.enrol.utils.ContactCredentialsDelegator;
import ish.oncourse.utils.StringUtilities;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

public class AddContactParser {

	public static final String FIELD_NAME_firstName = "firstName";
	public static final String FIELD_NAME_lastName = "lastName";
	public static final String FIELD_NAME_email = "email";

	public static final String LENGTH_FAILURE_MESSAGE = "The %s cannot exceed %d characters.";
	public static final String INVALID_EMAIL_MESSAGE = "The email address does not appear to be valid.";

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
		ContactErrorCode errorCode = getErrorCode(ContactInterface.FIRST_NAME_KEY);
		if (errorCode != null) {
			if (errorCode.equals(ContactErrorCode.firstNameNeedToBeProvided)) {
				errors.put(FIELD_NAME_firstName, "The student's first name is required.");
			} else if (errorCode.equals(ContactErrorCode.incorrectPropertyLength)) {
				errors.put(FIELD_NAME_firstName, String.format(LENGTH_FAILURE_MESSAGE, "first name", ContactValidator.Property.firstName));
			}
		}
	}

	private void parseLastName()
	{
		ContactErrorCode errorCode = getErrorCode(ContactInterface.LAST_NAME_KEY);
		if (errorCode != null) {
			if (errorCode.equals(ContactErrorCode.lastNameNeedToBeProvided)) {
				errors.put(FIELD_NAME_firstName, "The student's last name is required.");
			} else if (errorCode.equals(ContactErrorCode.incorrectPropertyLength)) {
				errors.put(FIELD_NAME_firstName, String.format(LENGTH_FAILURE_MESSAGE, "last name", ContactValidator.Property.lastName));
			}
		}
	}

	private void parseEmail()
	{
		ContactErrorCode errorCode = getErrorCode(ContactInterface.EMAIL_KEY);
		if (errorCode != null) {
			if (errorCode.equals(ContactErrorCode.incorrectEmailFormat)) {
				errors.put(FIELD_NAME_firstName, INVALID_EMAIL_MESSAGE);
			} else if (errorCode.equals(ContactErrorCode.incorrectPropertyLength)) {
				errors.put(FIELD_NAME_firstName, String.format(LENGTH_FAILURE_MESSAGE, "email name", ContactValidator.Property.email));
			}
		}
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

	private ContactErrorCode getErrorCode(String propertyKey) {
		ContactValidator contactValidator = ContactValidator.valueOf(ContactCredentialsDelegator.valueOf(contactCredentials));
		Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate();
		return errorCodeMap.get(propertyKey);
	}
}
