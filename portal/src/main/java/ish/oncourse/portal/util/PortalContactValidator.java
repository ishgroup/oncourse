package ish.oncourse.portal.util;


import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.components.ContactDetailStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomFieldType;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import ish.oncourse.util.contact.ValidateEmail;
import ish.oncourse.util.contact.ValidateSuburb;
import ish.oncourse.util.contact.WillowContactValidator;
import ish.oncourse.utils.ContactDelegator;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by anarut on 2/3/17.
 */
public class PortalContactValidator extends WillowContactValidator {
	
	private Messages contactDetailMessages;
	private Contact contact;
	private Map<String, String> customFieldContainer;
	private ContactFieldHelper contactFieldHelper;
	private ValidateHandler validateHandler;


	private boolean defaultCountry;

	private PortalContactValidator() {
		
	}
	
	public static PortalContactValidator valueOf(Contact contact,
	                                             Map<String, String> customFieldContainer,
	                                             ContactFieldHelper contactFieldHelper,
	                                             ValidateHandler validateHandler) {
		PortalContactValidator portalContactValidator = new PortalContactValidator();
		portalContactValidator.contactDetailMessages = MessagesImpl.forClass(ContactDetailStrings.class);
		portalContactValidator.contact = contact;
		portalContactValidator.customFieldContainer = customFieldContainer;
		portalContactValidator.contactFieldHelper = contactFieldHelper;
		portalContactValidator.validateHandler = validateHandler;
		portalContactValidator.defaultCountry = ICountryService.DEFAULT_COUNTRY_NAME.equals(contact.getCountry().getName());
		return portalContactValidator;
	}

	private boolean customFieldRequired(String customFieldKey) {
		return contactFieldHelper.isCustomFieldTypeRequired(getCustomFieldTypeByKey(customFieldKey));
	}

	private CustomFieldType getCustomFieldTypeByKey(String name) {
		for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
			if (fieldType.getKey().equals(name)) {
				return fieldType;
			}
		}

		return null;
	}

	private String getRequiredMessage(PreferenceController.FieldDescriptor fieldDescriptor) {
		String value = contactDetailMessages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.name()));
		
		return contactDetailMessages.format(ContactDetailStrings.KEY_ERROR_MESSAGE_fieldRequired, value);
	}
	
	
	
	public void validate() {
		Map<String, ContactErrorCode> errorCodes = ContactValidator.valueOf(ContactDelegator.valueOf(contact)).validate();
		
		ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>(validateHandler.getErrors());

		for (Map.Entry<String, String> customFieldEntry : customFieldContainer.entrySet()) {
			if (customFieldRequired(customFieldEntry.getKey()) && StringUtils.trimToNull(customFieldEntry.getValue()) == null) {
				errors.putIfAbsent(customFieldEntry.getKey(), contactDetailMessages.format(ContactDetailStrings.KEY_ERROR_MESSAGE_fieldRequired, customFieldEntry.getKey()));
			}
		}
		

		String emailErrorMessage = ValidateEmail.valueOf(errorCodes.get(ContactInterface.EMAIL_KEY), contactDetailMessages).getErrorMessage();
		if (emailErrorMessage != null) {
			errors.putIfAbsent(ContactInterface.EMAIL_KEY, emailErrorMessage);
		}

		if (StringUtils.isBlank(contact.getSuburb())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.suburb, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.suburb.name(), getRequiredMessage(PreferenceController.FieldDescriptor.suburb));
			}
		} else {
			String suburbErrorMessage = ValidateSuburb.valueOf(contact.getSuburb(), contactDetailMessages).getErrorMessage();

			if (suburbErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.suburb.name(), suburbErrorMessage);
			}
		}
		
		if (StringUtils.isBlank(contact.getPostcode())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.postcode, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.postcode.name(), getRequiredMessage(PreferenceController.FieldDescriptor.postcode));
			}
		} else {
			String postcodeErrorMessage = ValidatePostcode.valueOf(contact.getPostcode(), defaultCountry, errorCodes.get(ContactValidator.Property.postcode.name()), contactDetailMessages).getMessage();
			if (postcodeErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.postcode.name(), postcodeErrorMessage);
			}
		}

		if (StringUtils.isBlank(contact.getState())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.state, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.state.name(), getRequiredMessage(PreferenceController.FieldDescriptor.state));
			}
		} else {
			String stateErrorMessage = ValidateLength.valueOf(PreferenceController.FieldDescriptor.state, ContactValidator.Property.state.getLength(),
					errorCodes.get(ContactValidator.Property.state.name()), contactDetailMessages).getMessage();

			if (stateErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.state.name(), stateErrorMessage);
			}
		}
		
		

		if (StringUtils.isBlank(contact.getHomePhoneNumber())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.homePhoneNumber, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.homePhoneNumber.name(), getRequiredMessage(PreferenceController.FieldDescriptor.homePhoneNumber));
			}
		} else {
			PhoneValidator validator = PhoneValidator.homePhoneValidator(contact.getHomePhoneNumber(), defaultCountry,
					errorCodes.get(ContactValidator.Property.homePhone.name()), contactDetailMessages).validate();
			contact.setHomePhoneNumber(validator.getValue());
			String homePhoneErrorMessage = validator.getMessage();

			if (homePhoneErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.homePhoneNumber.name(), homePhoneErrorMessage);
			}
		}

		if (StringUtils.isBlank(contact.getMobilePhoneNumber())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.mobilePhoneNumber, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.mobilePhoneNumber.name(), getRequiredMessage(PreferenceController.FieldDescriptor.mobilePhoneNumber));
			}
		} else {
			MobilePhoneValidator mobileValidator = MobilePhoneValidator.valueOf(PreferenceController.FieldDescriptor.mobilePhoneNumber, ContactValidator.Property.mobilePhone.getLength(), contact.getMobilePhoneNumber(),
					defaultCountry, errorCodes, contactDetailMessages).validate();
			contact.setMobilePhoneNumber(mobileValidator.getValue());
			String mobilePhoneErrorMessage = mobileValidator.getMessage();

			if (mobilePhoneErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.mobilePhoneNumber.name(), mobilePhoneErrorMessage);
			}
		}

		if (StringUtils.isBlank(contact.getBusinessPhoneNumber())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.businessPhoneNumber, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.businessPhoneNumber.name(), getRequiredMessage(PreferenceController.FieldDescriptor.businessPhoneNumber));
			}
		} else {
			PhoneValidator validator = PhoneValidator.businessPhoneValidator(contact.getBusinessPhoneNumber(),
					defaultCountry, contactDetailMessages).validate();
			contact.setBusinessPhoneNumber(validator.getValue());
			
			String businessPhoneErrorMessage = validator.getMessage();

			if (businessPhoneErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.businessPhoneNumber.name(), businessPhoneErrorMessage);
			}
		}

		if (StringUtils.isBlank(contact.getFaxNumber())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.faxNumber, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.faxNumber.name(), getRequiredMessage(PreferenceController.FieldDescriptor.faxNumber));
			}
		} else {
			PhoneValidator validator = PhoneValidator.faxValidator(contact.getFaxNumber(), defaultCountry,
					errorCodes.get(ContactValidator.Property.fax.name()),
					contactDetailMessages).validate();
			contact.setFaxNumber(validator.getValue());
			
			String faxErrorMessage = validator.getMessage();

			if (faxErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.faxNumber.name(), faxErrorMessage);
			}
		}

		if (contact.getDateOfBirth() == null) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.dateOfBirth, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.dateOfBirth.name(), getRequiredMessage(PreferenceController.FieldDescriptor.dateOfBirth));
			}
		} else {
			String birthDateErrorMessage = DateOfBirthValidator.valueOf(contact.getDateOfBirth(), errorCodes.get(ContactInterface.BIRTH_DATE_KEY), contactDetailMessages).validate().getMessage();

			if (birthDateErrorMessage != null) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.dateOfBirth.name(), birthDateErrorMessage);
			}
		}

		if (contact.getCountry() == null) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.country, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.country.name(), getRequiredMessage(PreferenceController.FieldDescriptor.country));
			}
		}

		if (contact.getStudent() != null && StringUtils.isBlank(contact.getStudent().getSpecialNeeds())) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.specialNeeds, contact)) {
				errors.putIfAbsent(PreferenceController.FieldDescriptor.specialNeeds.name(), getRequiredMessage(PreferenceController.FieldDescriptor.specialNeeds));
			}
		}

		validateHandler.setErrors(errors);
	}
}
