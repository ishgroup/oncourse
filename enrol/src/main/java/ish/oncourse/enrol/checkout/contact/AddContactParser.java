package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.enrol.utils.ContactCredentialsDelegator;
import ish.oncourse.utils.StringUtilities;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

public class AddContactParser {

	public static final String FIELD_NAME_firstName = "firstName";
	public static final String FIELD_NAME_lastName = "lastName";
	public static final String FIELD_NAME_email = "email";

	public static final String LENGTH_FAILURE_MESSAGE = "The %s cannot exceed %d characters.";

	private Request request;
	private ContactCredentials contactCredentials;
	private boolean isCompany = false;

	private Map<String, String> errors = new HashMap<>();

	public void parse()
	{
		contactCredentials.setFirstName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_firstName)));
		contactCredentials.setLastName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_lastName)));
		contactCredentials.setEmail(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_email)));

		ContactValidator contactValidator = ContactValidator.valueOf(ContactCredentialsDelegator.valueOf(contactCredentials));
		Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate();

		parseLastName(errorCodeMap.get(ContactInterface.FIRST_NAME_KEY));
		parseEmail(errorCodeMap.get(ContactInterface.EMAIL_KEY));
		
		if (!isCompany) {
			parseFirstName(errorCodeMap.get(ContactInterface.FIRST_NAME_KEY));
		}
	}

	private void parseFirstName(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case firstNameNeedToBeProvided:
					errors.put(FIELD_NAME_firstName, "The student's first name is required.");
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_firstName, String.format(LENGTH_FAILURE_MESSAGE, "first", ContactValidator.Property.firstName.getLength()));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		if (StringUtils.containsAny(contactCredentials.getFirstName(), "0123456789")) {
			errors.put(FIELD_NAME_firstName, "The first name cannot contain number characters.");
		}
	}

	private void parseLastName(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case lastNameNeedToBeProvided:
					errors.put(FIELD_NAME_lastName, "The student's last name is required.");
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_lastName, String.format(LENGTH_FAILURE_MESSAGE, "last", ContactValidator.Property.lastName.getLength()));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		if (StringUtils.containsAny(contactCredentials.getLastName(), "0123456789")) {
			errors.put(FIELD_NAME_lastName, "The last name cannot contain number characters.");
		}
	}

	private void parseEmail(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case incorrectEmailFormat:
					errors.put(FIELD_NAME_email, "The email address does not appear to be valid.");
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_email, String.format(LENGTH_FAILURE_MESSAGE, "email", ContactValidator.Property.email.getLength()));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		if (contactCredentials.getEmail() == null) {
			errors.put(FIELD_NAME_email, "The student's email is required.");
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
}
