package ish.oncourse.enrol.utils;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.contact.ValidateSuburb;
import ish.oncourse.util.contact.WillowContactValidator;
import ish.oncourse.utils.ContactDelegator;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import ish.validation.ContactValidator.Property;
import org.apache.tapestry5.ioc.Messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrolContactValidator extends WillowContactValidator {

	private Contact contact;
	private List<String> visibleFields;
	private Messages messages;


	private boolean defaultCountry;
	private Map<String, ContactErrorCode> errorCodes;

	private Map<String, String> errors = new HashMap<>();

	public EnrolContactValidator validate() {
		defaultCountry = ICountryService.DEFAULT_COUNTRY_NAME.equals(contact.getCountry().getName());
		errorCodes = ContactValidator.valueOf(ContactDelegator.valueOf(contact)).validate();

		for (String visibleField : visibleFields) {
			FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(visibleField);
			String message = validate(fieldDescriptor);
			if (message != null) {
				errors.put(fieldDescriptor.name(), message);
			}
		}
		return this;
	}

	private String validate(FieldDescriptor fieldDescriptor) {
		switch (fieldDescriptor) {
			case street:
				return ValidateLength.valueOf(fieldDescriptor, Property.street.getLength(), errorCodes.get(Property.street.name()), messages).getMessage();
			case suburb:
				return ValidateSuburb.valueOf(contact.getSuburb(), messages).getErrorMessage();
			case state:
				return ValidateLength.valueOf(fieldDescriptor, Property.state.getLength(),
						errorCodes.get(Property.state.name()), messages).getMessage();
			case postcode:
				return ValidatePostcode.valueOf(contact.getPostcode(), defaultCountry,
						errorCodes.get(Property.postcode.name()), messages).getMessage();
			case homePhoneNumber:
				PhoneValidator validator = PhoneValidator.homePhoneValidator(contact.getHomePhoneNumber(), defaultCountry,
						errorCodes.get(Property.homePhone.name()), messages).validate();
				contact.setHomePhoneNumber(validator.getValue());
				return validator.getMessage();
			case businessPhoneNumber:
				validator = PhoneValidator.businessPhoneValidator(contact.getBusinessPhoneNumber(),
						defaultCountry, messages).validate();
				contact.setBusinessPhoneNumber(validator.getValue());
				return validator.getMessage();
			case faxNumber:
				validator = PhoneValidator.faxValidator(contact.getFaxNumber(), defaultCountry,
						errorCodes.get(Property.fax.name()),
						messages).validate();
				contact.setFaxNumber(validator.getValue());
				return validator.getMessage();
			case mobilePhoneNumber:
				MobilePhoneValidator mobileValidator = MobilePhoneValidator.valueOf(fieldDescriptor, Property.mobilePhone.getLength(), contact.getMobilePhoneNumber(),
						defaultCountry, errorCodes, messages).validate();
				contact.setMobilePhoneNumber(mobileValidator.getValue());
				return mobileValidator.getMessage();
			case dateOfBirth:
				return DateOfBirthValidator.valueOf(contact.getDateOfBirth(), errorCodes.get(ContactInterface.BIRTH_DATE_KEY), messages).validate().getMessage();
			case country:
				return null;
			case specialNeeds:
				return null;
			case abn:
				return null;
			case isMale:
				return null;
			default:
				throw new IllegalArgumentException(String.format("Field descriptor %s is not supported", fieldDescriptor));
		}

	}

	public Map<String, String> getErrors() {
		return errors;
	}


	public static EnrolContactValidator valueOf(Contact contact, List<String> visibleFields, Messages messages) {
		EnrolContactValidator result = new EnrolContactValidator();
		result.contact = contact;
		result.visibleFields = visibleFields;
		result.messages = messages;
		return result;
	}
	
}
