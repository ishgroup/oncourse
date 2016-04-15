package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.enrol.utils.ContactCredentialsDelegator;
import ish.oncourse.utils.StringUtilities;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;

import static ish.validation.ContactErrorCode.*;

public class AddContactValidator {

	public static final String FIELD_NAME_firstName = "firstName";
	public static final String FIELD_NAME_lastName = "lastName";
	public static final String FIELD_NAME_email = "email";

	protected static final Map<ContactErrorCode, String> ERROR_MESSAGES = new HashMap<>();
	static {
		ERROR_MESSAGES.put(firstNameNeedToBeProvided, "The student's first name is required.");
		ERROR_MESSAGES.put(lastNameNeedToBeProvided, "The student's last name is required.");
		ERROR_MESSAGES.put(incorrectEmailFormat, "The email address does not appear to be valid.");
		ERROR_MESSAGES.put(incorrectPropertyLength, "The %s cannot exceed %d characters.");
	}

	private Request request;
	private ContactCredentials contactCredentials;
	private boolean isCompany;

	private Map<String, String> errors = new HashMap<>();

	private AddContactValidator() {
	}

	public static AddContactValidator valueOf(ContactCredentials contactCredentials, Request request, boolean isCompany) {
		AddContactValidator addContactParser = new AddContactValidator();
		addContactParser.contactCredentials = contactCredentials;
		addContactParser.request = request;
		addContactParser.isCompany = isCompany;

		contactCredentials.setFirstName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_firstName)));
		contactCredentials.setLastName(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_lastName)));
		contactCredentials.setEmail(StringUtilities.cutToNull(request.getParameter(FIELD_NAME_email)));

		return addContactParser;
	}

	public void validate()
	{
		ContactValidator contactValidator = ContactValidator.valueOf(ContactCredentialsDelegator.valueOf(contactCredentials));
		Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate();

		validateLastName(errorCodeMap.get(ContactInterface.LAST_NAME_KEY));
		validateEmail(errorCodeMap.get(ContactInterface.EMAIL_KEY));
		
		if (!isCompany) {
			validateFirstName(errorCodeMap.get(ContactInterface.FIRST_NAME_KEY));
		}
	}

	private void validateFirstName(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case firstNameNeedToBeProvided:
					errors.put(FIELD_NAME_firstName, ERROR_MESSAGES.get(firstNameNeedToBeProvided));
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_firstName, String.format(ERROR_MESSAGES.get(incorrectPropertyLength), "first name", ContactValidator.Property.firstName.getLength()));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
	}

	private void validateLastName(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case lastNameNeedToBeProvided:
					errors.put(FIELD_NAME_lastName, ERROR_MESSAGES.get(lastNameNeedToBeProvided));
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_lastName, String.format(ERROR_MESSAGES.get(incorrectPropertyLength), "last name", ContactValidator.Property.lastName.getLength()));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
	}

	private void validateEmail(ContactErrorCode errorCode)
	{
		if (errorCode != null) {
			switch (errorCode) {
				case incorrectEmailFormat:
					errors.put(FIELD_NAME_email, ERROR_MESSAGES.get(incorrectEmailFormat));
					break;
				case incorrectPropertyLength:
					errors.put(FIELD_NAME_email, String.format(ERROR_MESSAGES.get(incorrectPropertyLength), "email", ContactValidator.Property.email.getLength()));
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

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
