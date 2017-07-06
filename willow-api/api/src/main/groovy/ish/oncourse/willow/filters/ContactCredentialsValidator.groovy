package ish.oncourse.willow.filters

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.util.contact.CommonContactValidator
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.web.CreateContactParams
import ish.oncourse.willow.service.ContactValidation
import ish.validation.ValidationUtil
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.BadRequestException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ReaderInterceptor
import javax.ws.rs.ext.ReaderInterceptorContext

import static ish.oncourse.common.field.FieldProperty.*
import static ish.validation.ContactValidator.Property.*

@ContactValidation
class ContactCredentialsValidator implements ReaderInterceptor {

    private static final Map<FieldProperty, ErrorHolder> IS_BLANK_ERRORS =
            [(FIRST_NAME) : new ErrorHolder(emptyError: 'The student\'s first name is required.', maxLength: firstName.length),
             (LAST_NAME) : new ErrorHolder(emptyError: 'The student\'s last name is required.', maxLength: lastName.length),
             (EMAIL_ADDRESS) : new ErrorHolder(emptyError: 'The student\'s email is required.', maxLength: email.length) ]

    private static final String INVALID_EMAIL = 'The email address does not appear to be valid.'

    @Override
    Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        CreateContactParams params = context.proceed() as CreateContactParams

        if (!params.fieldSet) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new ValidationError(formErrors: ['\'fieldSet\' parameter is required']))
                    .build())
        }
        
        ValidationError error = validate(params)
        if (error.fieldsErrors.empty) {
            return params
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(error).build())
        } 
        
    }
    
    private ValidationError validate(CreateContactParams contactParams) {
        ValidationError validationError = new ValidationError()

        if (!contactParams.company) {
            validationError.fieldsErrors.addAll(emptyCheck(contactParams.firstName, FIRST_NAME))
        }
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
            fieldsErrors << new FieldError(name: field.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, field.displayName,  IS_BLANK_ERRORS.get(field).maxLength))
        }

        fieldsErrors
    }

    static class ErrorHolder {
        int maxLength
        String emptyError
    }
}
