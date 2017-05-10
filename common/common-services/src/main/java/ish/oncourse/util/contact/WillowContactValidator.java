package ish.oncourse.util.contact;

import ish.oncourse.components.ContactDetailStrings;
import ish.oncourse.services.preference.PreferenceController;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;


import java.util.Date;
import java.util.Map;

public class WillowContactValidator {

	
	
	public static class DateOfBirthValidator {
		private Date value;
		private ContactErrorCode errorCode;
		private Messages messages;

		private String message;

		public DateOfBirthValidator validate() {
			if (value != null) {
				if (errorCode != null) {
					if (errorCode == ContactErrorCode.birthDateCanNotBeInFuture) {
						message = messages.get(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast);
					}
				}
				if (message == null && CommonContactValidator.MIN_DATE_OF_BIRTH.compareTo(value) > 0) {
					message = messages.get(ContactDetailStrings.KEY_ERROR_MESSAGE_birthdate_old);
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
		private PreferenceController.FieldDescriptor fieldDescriptor;
		private int propertyLength;
		private boolean defaultCountry = true;
		private Map<String, ContactErrorCode> errorMap;
		private Messages messages;

		private String message;

		public MobilePhoneValidator validate() {
			if (!StringUtils.isBlank(value)) {
				message = ValidateLength.valueOf(fieldDescriptor, propertyLength,
						errorMap.get(ContactValidator.Property.mobilePhone.name()), messages).getMessage();
				if (message == null && defaultCountry) {
					ish.oncourse.utils.PhoneValidator.MobileValidator validator = ish.oncourse.utils.PhoneValidator.MobileValidator.valueOf(value).validate();
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

		public static MobilePhoneValidator valueOf(PreferenceController.FieldDescriptor fieldDescriptor, int propertyLength, String value,
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
		private PreferenceController.FieldDescriptor fieldDescriptor;
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

		private static PhoneValidator valueOf(PreferenceController.FieldDescriptor fieldDescriptor, int propertyLength, String value, String propertyDisplayName,
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

		public static PhoneValidator homePhoneValidator(String value, boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			return valueOf(PreferenceController.FieldDescriptor.homePhoneNumber, ContactValidator.Property.homePhone.getLength(), value, "home phone", defaultCountry, errorCode, messages);
		}

		public static PhoneValidator businessPhoneValidator(String value, boolean defaultCountry, Messages messages) {
			value = StringUtils.trimToNull(value);
			return valueOf(PreferenceController.FieldDescriptor.businessPhoneNumber, 20, value, "work phone", defaultCountry,
					(value != null && value.length() > 20) ? ContactErrorCode.incorrectPropertyLength : null, messages);
		}

		public static PhoneValidator faxValidator(String value, boolean defaultCountry, ContactErrorCode errorCode, Messages messages) {
			return valueOf(PreferenceController.FieldDescriptor.faxNumber, ContactValidator.Property.fax.getLength(), value, "fax", defaultCountry, errorCode, messages);
		}
	}

	public static class ValidatePostcode {
		private ContactErrorCode errorCode;
		private Messages messages;
		private boolean defaultCountry = true;
		private String value;


		public String getMessage() {
			String message = ValidateLength.valueOf(PreferenceController.FieldDescriptor.postcode, ContactValidator.Property.postcode.getLength(), errorCode, messages).getMessage();

			if (message != null) {
				return message;
			}
			if (defaultCountry) {
				if (!StringUtils.isBlank(value) && !value.matches("(\\d){4}")) {
					return messages.get(ContactDetailStrings.KEY_ERROR_MESSAGE_message_postcode_4_digits);
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
		private PreferenceController.FieldDescriptor fieldDescriptor;
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

		public static ValidateLength valueOf(PreferenceController.FieldDescriptor fieldDescriptor, int length, ContactErrorCode errorCode, Messages messages) {
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
