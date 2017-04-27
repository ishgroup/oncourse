package ish.oncourse.willow.service.impl

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.willow.model.CreateContactParams
import ish.oncourse.willow.model.FieldError
import ish.oncourse.willow.model.ValidationError
import ish.validation.ValidationUtil
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.common.field.FieldProperty.*
import static ish.validation.ContactValidator.Property.*


class ContactCredentialsValidator {


    private static final Map<FieldProperty, ErrorHolder> IS_BLANK_ERRORS = 
            [(FIRST_NAME) : new ErrorHolder(emptyError: 'The student\'s first name is required.', maxLength: firstName.length),
             (LAST_NAME) : new ErrorHolder(emptyError: 'The student\'s last name is required.', maxLength: lastName.length),
             (EMAIL_ADDRESS) : new ErrorHolder(emptyError: 'The student\'s email is required.', maxLength: email.length) ] 
  


    private static final String INCORRECT_PROPERTY_LENGTH ='The %s cannot exceed %d characters.'
    private static final String INVALID_EMAIL = 'The email address does not appear to be valid.'
    


    ValidationError validate(CreateContactParams contactParams) {
        ValidationError validationError = new ValidationError()

        validationError.fieldsErrors.addAll( emptyCheck(contactParams.firstName, FIRST_NAME))
        validationError.fieldsErrors.addAll( emptyCheck(contactParams.lastName, LAST_NAME))
        
        //if email is not empty and length is normal (addAll() return 'false') - continue validate  email  
        if (!validationError.fieldsErrors.addAll(emptyCheck(contactParams.email, EMAIL_ADDRESS)) && !ValidationUtil.isValidEmailAddress(contactParams.email)) {
            validationError.fieldsErrors << new FieldError(name: EMAIL_ADDRESS.key, error: INVALID_EMAIL)
        }

        validationError
    }
    
    private List <FieldError>  emptyCheck(String value, FieldProperty field) {
        List <FieldError> fieldsErrors = []
        
        if (StringUtils.isBlank(value)) {
            fieldsErrors << new FieldError(name: field.key, error: IS_BLANK_ERRORS.get(field).emptyError)
        } else if (value.length() >  IS_BLANK_ERRORS.get(field).maxLength) {
            fieldsErrors << new FieldError(name: field.key, error: String.format(INCORRECT_PROPERTY_LENGTH, field.displayName,  IS_BLANK_ERRORS.get(field).maxLength))
        }

        fieldsErrors
    }

    static class ErrorHolder {
        int maxLength
        String emptyError
    }
}
