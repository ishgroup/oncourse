package ish.oncourse.enrol.utils;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.utils.ContactDelegator;
import ish.oncourse.utils.PhoneValidator.MobileValidator;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import ish.validation.ContactValidator.Property;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.enrol.services.Constants.MIN_DATE_OF_BIRTH;

public class EnrolContactValidator {

	public static final String KEY_ERROR_MESSAGE_fieldRequired = "message-fieldRequired";
	public static final String KEY_ERROR_MESSAGE_birthdate_hint = "message-birthdateHint";
	public static final String KEY_ERROR_MESSAGE_birthdate_old = "message-oldbirthdate";

	public static final String KEY_ERROR_dateOfBirth_youngAge = "message-dateOfBirth-youngAge";
	public static final String KEY_ERROR_dateOfBirth_shouldBeInPast = "message-dateOfBirth-shouldBeInPast";
	public static final String KEY_ERROR_error_countryOfBirth = "message-countryOfBirth";

	public static final String KEY_ERROR_MESSAGE_message_postcode_4_digits = "message-postcode-4-digits";

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
				return contact.validateSuburb();
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

	public static class DateOfBirthValidator {
		private Date value;
		private ContactErrorCode errorCode;
		private Messages messages;

		private String message;

		public DateOfBirthValidator validate() {
			if (value != null) {
				if (errorCode != null) {
					if (errorCode == ContactErrorCode.birthDateCanNotBeInFuture) {
						message = messages.get(KEY_ERROR_dateOfBirth_shouldBeInPast);
					}
				}
				if (message == null && MIN_DATE_OF_BIRTH.compareTo(value) > 0) {
					message = messages.get(KEY_ERROR_MESSAGE_birthdate_old);
				}
			}
			return this;
		}

		public String getMessage() {
			return message;
		}

		public static DateOfBirthValidator valueOf(Date value,
												   ContactErrorCode errorCode,
												   Messages messages) {
			DateOfBirthValidator result = new DateOfBirthValidator();
			result.value = value;
			result.errorCode = errorCode;
			result.messages = messages;
			return result;
		}
	}

	public static class MobilePhoneValidator {
		private String value;
		private FieldDescriptor fieldDescriptor;
		private int propertyLength;
		private boolean defaultCountry = true;
		private Map<String, ContactErrorCode> errorMap;
		private Messages messages;

		private String message;

		public MobilePhoneValidator validate() {
			if (!StringUtils.isBlank(value)) {
				message = ValidateLength.valueOf(fieldDescriptor, propertyLength,
						errorMap.get(Property.mobilePhone.name()), messages).getMessage();
				if (message == null && defaultCountry) {
					MobileValidator validator = MobileValidator.valueOf(value).validate();
					value = validator.getValue();
					message = validator.getMessage();
				}
			}
			return this;
		}

		public String getMessage() {
			return message;
		}

		public String getValue() {
			return value;
		}

		public static MobilePhoneValidator valueOf(FieldDescriptor fieldDescriptor, int propertyLength, String value,
												   boolean defaultCountry, Map<String, ContactErrorCode> errorMap, Messages messages) {
			MobilePhoneValidator result = new MobilePhoneValidator();
			result.fieldDescriptor = fieldDescriptor;
			result.propertyLength = propertyLength;
			result.value = value;
			result.defaultCountry = defaultCountry;
			result.errorMap = errorMap;
			result.messages = messages;
			return result;
		}
	}


	public static class PhoneValidator {
		private String value;
		private FieldDescriptor fieldDescriptor;
		private int propertyLength;
		private String propertyDisplayName;
		private boolean defaultCountry = true;
		private ContactErrorCode errorCode;
		private Messages messages;

		private String message;

		public PhoneValidator validate() {
			if (!StringUtils.isBlank(value)) {
				message = ValidateLength.valueOf(fieldDescriptor, propertyLength, errorCode, messages).getMessage();
				if (message == null && defaultCountry) {
					ish.oncourse.utils.PhoneValidator.Validator validator = ish.oncourse.utils.PhoneValidator.Validator.valueOf(value, propertyDisplayName).validate();
					value = validator.getValue();
					message = validator.getMessage();
				}
			}
			return this;
		}

		public String getMessage() {
			return message;
		}

		public String getValue() {
			return value;
		}

		private static PhoneValidator valueOf(FieldDescriptor fieldDescriptor, int propertyLength, String value, String propertyDisplayName,
											  boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			PhoneValidator result = new PhoneValidator();
			result.fieldDescriptor = fieldDescriptor;
			result.propertyLength = propertyLength;
			result.value = value;
			result.propertyDisplayName = propertyDisplayName;
			result.defaultCountry = defaultCountry;
			result.errorCode = errorCode;
			result.messages = messages;
			return result;
		}

		static PhoneValidator homePhoneValidator(String value, boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			return valueOf(FieldDescriptor.homePhoneNumber, Property.homePhone.getLength(), value, "home phone", defaultCountry, errorCode, messages);
		}

		static PhoneValidator businessPhoneValidator(String value, boolean defaultCountry, Messages messages) {
			value = StringUtils.trimToNull(value);
			return valueOf(FieldDescriptor.businessPhoneNumber, 20, value, "work phone", defaultCountry,
					(value != null && value.length() > 20) ? ContactErrorCode.incorrectPropertyLength : null, messages);
		}

		static PhoneValidator faxValidator(String value, boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			return valueOf(FieldDescriptor.faxNumber, Property.fax.getLength(), value, "fax", defaultCountry, errorCode, messages);
		}


	}

	public static class ValidatePostcode {
		private ContactErrorCode errorCode;
		private Messages messages;
		private boolean defaultCountry = true;
		private String value;


		public String getMessage() {
			String message = ValidateLength.valueOf(FieldDescriptor.postcode, Property.postcode.getLength(), errorCode, messages).getMessage();

			if (message != null) {
				return message;
			}
			if (defaultCountry) {
				if (!StringUtils.isBlank(value) && !value.matches("(\\d){4}")) {
					return messages.get("message-postcode-4-digits");
				}
			}
			return null;
		}

		public static ValidatePostcode valueOf(String value, boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			ValidatePostcode result = new ValidatePostcode();
			result.value = value;
			result.defaultCountry = defaultCountry;
			result.errorCode = errorCode;
			result.messages = messages;
			return result;
		}
	}

	public static class ValidateLength {
		private FieldDescriptor fieldDescriptor;
		private int length;

		private ContactErrorCode errorCode;
		private Messages messages;

		private String messageKey;

		public String getMessage() {
			if (errorCode != null) {
				if (errorCode == ContactErrorCode.incorrectPropertyLength) {
					return messages.format(messageKey, length);
				}
			}
			return null;
		}

		public static ValidateLength valueOf(FieldDescriptor fieldDescriptor, int length, ContactErrorCode errorCode, Messages messages) {
			ValidateLength result = new ValidateLength();
			result.fieldDescriptor = fieldDescriptor;
			result.length = length;
			result.messageKey = String.format("message-%s-length", result.fieldDescriptor.name());
			result.errorCode = errorCode;
			result.messages = messages;
			return result;
		}
	}
}
