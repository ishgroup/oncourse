package ish.oncourse.util.contact;

import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.tapestry5.ioc.Messages;

import static ish.oncourse.components.ContactDetailStrings.KEY_ERROR_MESSAGE_emailInvalidFormat;
import static ish.oncourse.components.ContactDetailStrings.KEY_ERROR_MESSAGE_emailLength;

/**
 * Created by anarut on 2/3/17.
 */
public class ValidateEmail {

	private ContactErrorCode errorCode;
	private Messages errorMessages;
			
	private ValidateEmail() {
		
	}
	
	public static ValidateEmail valueOf(ContactErrorCode errorCode, Messages errorMessages) {
		ValidateEmail validateEmail = new ValidateEmail();
		validateEmail.errorCode = errorCode;
		validateEmail.errorMessages = errorMessages;
		return validateEmail;
	}
	
	public String getErrorMessage() {
		if (errorCode == null) {
			return null;
		} else {
			switch (errorCode) {
				case incorrectEmailFormat:
					return errorMessages.get(KEY_ERROR_MESSAGE_emailInvalidFormat);
				case incorrectPropertyLength:
					return errorMessages.format(KEY_ERROR_MESSAGE_emailLength, ContactValidator.Property.email.getLength());
				default:
					throw new IllegalArgumentException(String.format("Invalid error code for contact email validation : %s", errorCode));
			}
		}
	}
}
