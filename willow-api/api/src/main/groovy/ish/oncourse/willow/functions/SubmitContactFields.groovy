package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.common.types.TypesUtil
import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.cayenne.StudentInterface
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.util.FormatUtils
import ish.oncourse.util.contact.CommonContactValidator
import ish.oncourse.utils.PhoneValidator
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.field.Field
import ish.validation.StudentErrorCode
import ish.validation.StudentValidator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.ParseException

import static ish.oncourse.willow.functions.ContactDetailsBuilder.getContext
import static ish.oncourse.willow.model.field.DataType.*
import static ish.oncourse.willow.model.field.DataType.BOOLEAN
import static ish.oncourse.willow.model.field.DataType.STRING
import static ish.validation.ContactValidator.Property.*

@CompileStatic
class SubmitContactFields {

    final static Logger logger = LoggerFactory.getLogger(CreateOrGetContact.class)
    
    ValidationError errors = new ValidationError()
    ObjectContext objectContext
    boolean isDefaultCountry = false
    
    SubmitContactFields submitContactFields(Contact contact, List<Field> fields) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        PropertyGetSet getSet

        Field country = fields.find { FieldProperty.COUNTRY.key == it.key }
        if (!country || CommonContactValidator.DEFAULT_COUNTRY_NAME == country.value) {
            isDefaultCountry = true
        }
        
        fields.each { f ->

            FieldProperty  property = FieldProperty.getByKey(f.key)
            if (!property) {
                logger.error "unsupported property ${f.name}".toString()
                errors.formErrors << "unsupported property ${f.name}".toString()
                return 
            }
            
            Object value = normalizeValue(property, f)
            if (value != null) {
                FieldError error = validateValue(property, value)
                if (error) {
                    errors.fieldsErrors << error
                } else {
                    getSet = factory.get([getProperty: {f.key}] as FieldInterface, getContext.call(property.contextType, contact))
                    getSet.set(value)
                }
                
            }
            
        }
        
        if (contact.getCountry() == null) {
            contact.setCountry(getCountryBy(CommonContactValidator.DEFAULT_COUNTRY_NAME))
        }
        
        this
    }
    
    
    private Object normalizeValue(FieldProperty  property, Field f) {
        Object result = null
        if (StringUtils.trimToNull(f.value)) {
            switch (f.dataType) {
                case EMAIL:
                case STRING:
                    result = f.value
                    result = normalizePhone(property, result)
                    break
                case BOOLEAN:
                    result = Boolean.valueOf(f.value)
                    break
                case DATE:
                case DATETIME:
                    try {
                        result =  DateUtils.truncate(Date.parse(FormatUtils.DATE_FIELD_PARSE_FORMAT, f.value), Calendar.DAY_OF_MONTH)

                    } catch (ParseException e) {
                        result = null
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Enter your ${f.name} in the form DD/MM/YYYY")
                    }
                    break
                case INTEGER:
                    if (org.apache.commons.lang.StringUtils.isNumeric(f.value)) {
                        result = Integer.valueOf(f.value)
                    } else {
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Enter your ${f.name} in the form DD/MM/YYYY")
                    }
                    break
                case COUNTRY:
                    result = getCountryBy(f.value)
                    if (!result) {
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Incorrect format of ${f.name}")
                    }
                    break
                case LANGUAGE:
                    result = getLanguageBy(f.value)
                    if (!result) {
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Language name ${f.value} is incorrect")
                    }
                    break
                case ENUM:
                    if (org.apache.commons.lang.StringUtils.isNumeric(f.value)) {
                        result = TypesUtil.getEnumForDatabaseValue(f.value, this.class.classLoader.loadClass("ish.common.types.$f.enumType"))
                    } else {
                        errors.fieldsErrors << new FieldError(name: f.key, error: "${f.name} is incorrect")
                    }
                    break
                default:
                    result = null
                    logger.error("unsupported type for field value: $f")
                    errors.formErrors << "unsupported type for field value: ${f.name}".toString()
            }

        } else if (f.mandatory) {
            logger.error("${f.name} required: ${f}")
            errors.fieldsErrors << new FieldError(name: f.key, error: "${f.name} is required")
        } 
        result
    }

    private Object normalizePhone(FieldProperty property, Object value) {
        if ([FieldProperty.HOME_PHONE_NUMBER, FieldProperty.BUSINESS_PHONE_NUMBER, FieldProperty.FAX_NUMBER, FieldProperty.MOBILE_PHONE_NUMBER].contains(property)) {
           return StringUtils.removePattern(value as String, "[^0-9]")
        }
        value
    }

    private FieldError validateValue(FieldProperty  property, Object value) {
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
                    PhoneValidator.Validator validator = PhoneValidator.Validator.valueOf(value as String, property.displayName).validate()
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
            default: 
                return null
        }
        
        if (stringError) {
            return new FieldError(name: property.key, error: stringError)
        } 
        null
    }
    
    private String validateYearSchoolCompleted(Integer year) {
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

    private String validateBirthDay(Date dob) {
        Date birthDateTruncated = DateUtils.truncate(dob as Date, Calendar.DAY_OF_MONTH)
        Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
        if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
            return "Date of birth can not be in future"
        } else if (CommonContactValidator.MIN_DATE_OF_BIRTH.compareTo(dob) > 0){
            return'Only date of birth in format DD/MM/YYYY and after 01/01/1900 are valid'
        }
        null
    }


    private Country getCountryBy(String name) {
        ObjectSelect.query(Country).where(Country.NAME.eq(name))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(Country.class.simpleName)
                .selectFirst(objectContext)
    }

    private Language getLanguageBy(String name) {
        ObjectSelect.query(Language).where(Language.NAME.eq(name))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(Language.class.simpleName)
                .selectFirst(objectContext)
    }
}
