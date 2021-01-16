package ish.oncourse.willow.functions.field

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.StudentInterface
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.College
import ish.oncourse.util.contact.CommonContactValidator
import ish.oncourse.utils.PhoneValidator
import ish.oncourse.willow.model.common.FieldError
import ish.validation.StudentErrorCode
import ish.validation.StudentValidator
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static ish.oncourse.willow.model.field.DataType.ENUM
import static ish.validation.ContactValidator.Property.*

@CompileStatic
class FieldValueValidator {
    final static Logger logger = LoggerFactory.getLogger(FieldValueValidator)

    FieldProperty property
    String fieldKey
    ObjectContext context
    Boolean isDefaultCountry

    College college

    FieldValueValidator(FieldProperty property, String fieldKey, ObjectContext context, College college, Boolean isDefaultCountry = false) {
        this.property = property
        this.fieldKey = fieldKey
        this.context = context
        this.isDefaultCountry = isDefaultCountry
        this.college = college
        
    }

    FieldError validate(Object value) {
        String stringError = null
        switch (property) {
            case FieldProperty.STREET:
                if ((value as String).length() > street.length) {
                    stringError =  String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, street.length)
                }
                break
            case FieldProperty.SUBURB:
                if (isDefaultCountry && (value as String).split("\\d").length != 1) {
                    stringError ='A suburb name cannot contain numeric digits.'
                }
                break
            case FieldProperty.POSTCODE:
                if ((value as String).length() > postcode.length) {
                    stringError = String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, postcode.length)
                } else if (isDefaultCountry && !(value as String).matches("(\\d){4}")) {
                    stringError ='Enter 4 digit postcode for Australian postcodes.'
                }
                break
            case FieldProperty.STATE:
                if ((value as String).length() > state.length) {
                    stringError = String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, state.length)
                }
                break
            case FieldProperty.HOME_PHONE_NUMBER:
            case FieldProperty.BUSINESS_PHONE_NUMBER:
            case FieldProperty.FAX_NUMBER:
                if (isDefaultCountry) {
                    PhoneValidator.Validator validator = PhoneValidator.Validator.valueOf(value as String, property.displayName.split(' ')[0]).validate()
                    stringError = validator.message
                } else {
                    if ((value as String).length() > 20) {
                        return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, 20))
                    }
                }
                break
            case FieldProperty.MOBILE_PHONE_NUMBER:
                if (isDefaultCountry) {
                    PhoneValidator.MobileValidator validator = PhoneValidator.MobileValidator.valueOf(value as String).validate()
                    stringError = validator.message
                } else {
                    if ((value as String).length() > mobilePhone.length) {
                        stringError = String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, mobilePhone.length)
                    }
                }
                break
            case FieldProperty.DATE_OF_BIRTH:
                stringError = validateBirthDay(value as Date)
                break
            case FieldProperty.YEAR_SCHOOL_COMPLETED:
                stringError = validateYearSchoolCompleted(value as Integer)
                break
            case FieldProperty.STUDENTS_COUNT:
                Integer studentsCount = value as Integer
                if (studentsCount < 1 || studentsCount > 30) {
                    stringError << 'You should enter numbers from 1 to 30. If you have larger groups please add the details in the notes.'
                }
                break
            case FieldProperty.CUSTOM_FIELD_CONTACT:
            case FieldProperty.CUSTOM_FIELD_ENROLMENT:
            case FieldProperty.CUSTOM_FIELD_APPLICATION:
            case FieldProperty.CUSTOM_FIELD_WAITING_LIST:
                ProcessCustomFieldType processor = new ProcessCustomFieldType(fieldKey, context, college).process()
                if (ENUM == processor.dataType && !processor.items.collect { it.key }.contains(value)) {
                    stringError = 'Please select a value from the drop-down list'
                }
                break
            default:
                return null
        }

        if (stringError) {
            return new FieldError(name: fieldKey, error: stringError)
        }
        null
    }

    private static String validateYearSchoolCompleted(Integer year) {
        Map<String, StudentErrorCode> result = StudentValidator.valueOf([getContact: {null}, getYearSchoolCompleted: {year}] as StudentInterface).validate()

        StudentErrorCode code = result.get(StudentInterface.YEAR_SCHOOL_COMPLETED_KEY)

        if (code) {
            switch (code) {
                case StudentErrorCode.yearSchoolCompletedInFuture:
                    return 'Year school completed cannot be in the future if supplied.'
                case StudentErrorCode.yearSchoolCompletedBefore1940:
                    return 'Year school completed if supplied should be within not earlier than 1940.'
                default:
                    throw new IllegalArgumentException("Unsupported error code for student: $code")
            }
        }
        null
    }


    private static String validateBirthDay(Date dob) {
        Date birthDateTruncated = DateUtils.truncate(dob as Date, Calendar.DAY_OF_MONTH)
        Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
        if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
            return "Date of birth can not be in future"
        } else if (CommonContactValidator.MIN_DATE_OF_BIRTH > dob){
            return'Only date of birth in format DD/MM/YYYY and after 01/01/1900 are valid'
        }
        null
    }
}
