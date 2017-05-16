package ish.oncourse.willow.service

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.willow.filters.ContactCredentialsValidator
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.web.CreateContactParams

import org.junit.Test

import javax.ws.rs.BadRequestException
import javax.ws.rs.ext.ReaderInterceptorContext

import static ish.oncourse.common.field.FieldProperty.*
import static ish.oncourse.willow.model.field.FieldSet.ENROLMENT
import static ish.validation.ContactValidator.Property.email
import static ish.validation.ContactValidator.Property.firstName
import static ish.validation.ContactValidator.Property.lastName
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ContactValidatorTest {
    
    
    @Test
    void validateCredantials() {
        
        ContactCredentialsValidator validator = new  ContactCredentialsValidator()

        try {
            validator.aroundReadFrom([proceed: {new CreateContactParams(firstName: 'firstName', lastName: 'lastName', email: 'email@com.au')}] as ReaderInterceptorContext)
        } catch (BadRequestException e) {
            assertEquals(400, e.response.status)
            ValidationError validationError = e.response.entity as ValidationError
            assertTrue(validationError.fieldsErrors.empty)
            assertEquals(1l, validationError.formErrors.size())
            assertEquals('\'fieldSet\' parameter is required', validationError.formErrors[0])
        }

        try {
            validator.aroundReadFrom([proceed: {new CreateContactParams(firstName: 'firstName', lastName: 'lastName', email: 'emailcom.au', fieldSet: ENROLMENT)}] as ReaderInterceptorContext)
        } catch (BadRequestException e) {
            assertEquals(400, e.response.status)
            ValidationError validationError = e.response.entity as ValidationError
            assertTrue(validationError.formErrors.empty)
            assertEquals(1l, validationError.fieldsErrors.size())
            assertEquals(FieldProperty.EMAIL_ADDRESS.key, validationError.fieldsErrors[0].name)
            assertEquals('The email address does not appear to be valid.', validationError.fieldsErrors[0].error)
        }

        try {
            validator.aroundReadFrom([proceed: {new CreateContactParams(firstName: '', lastName: '', email: '', fieldSet: ENROLMENT)}] as ReaderInterceptorContext)
        } catch (BadRequestException e) {
            assertEquals(400, e.response.status)
            ValidationError validationError = e.response.entity as ValidationError
            assertTrue(validationError.formErrors.empty)
            assertEquals(3l, validationError.fieldsErrors.size())
            assertEquals(FIRST_NAME.key, validationError.fieldsErrors[0].name)
            assertEquals('The student\'s first name is required.', validationError.fieldsErrors[0].error)
            assertEquals(LAST_NAME.key, validationError.fieldsErrors[1].name)
            assertEquals('The student\'s last name is required.', validationError.fieldsErrors[1].error)
            assertEquals(EMAIL_ADDRESS.key, validationError.fieldsErrors[2].name)
            assertEquals('The student\'s email is required.', validationError.fieldsErrors[2].error)
        }


        try {
            validator.aroundReadFrom([proceed: {new CreateContactParams(
                    firstName: 'veryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryvelongname',
                    lastName: 'veryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryvelongname', 
                    email : 'veryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryve@cam.au',
                    fieldSet: ENROLMENT)}] as ReaderInterceptorContext)
        } catch (BadRequestException e) {
            assertEquals(400, e.response.status)
            ValidationError validationError = e.response.entity as ValidationError
            assertTrue(validationError.formErrors.empty)
            assertEquals(3l, validationError.fieldsErrors.size())
            assertEquals(FIRST_NAME.key, validationError.fieldsErrors[0].name)
            assertEquals("The ${FIRST_NAME.displayName} cannot exceed ${firstName.length} characters." as String, validationError.fieldsErrors[0].error)
            assertEquals(LAST_NAME.key, validationError.fieldsErrors[1].name)
            assertEquals("The ${LAST_NAME.displayName} cannot exceed ${lastName.length} characters." as String, validationError.fieldsErrors[1].error)
            assertEquals(EMAIL_ADDRESS.key, validationError.fieldsErrors[2].name)
            assertEquals("The ${EMAIL_ADDRESS.displayName} cannot exceed ${email.length} characters." as String, validationError.fieldsErrors[2].error)
        }
    }
}
