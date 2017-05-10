package ish.oncourse.willow.functions

import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import ish.common.types.TypesUtil
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.util.FormatUtils
import ish.oncourse.util.contact.CommonContactValidator
import ish.oncourse.utils.PhoneValidator
import ish.oncourse.willow.filters.ContactCredentialsValidator
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import ish.validation.ContactValidator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
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



    void submitContactFields(Contact contact, List<Field> fields) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.common')
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
            
            Object value = normalizeValue(f)
            FieldError error = validateValue(property, value)
            if (error) {
                errors.fieldsErrors << error
            }
            
            if (value) {
                getSet = factory.get([property: {property.key}] as FieldInterface, getContext.call(property.contextType, contact))
                getSet.set(value)
            }
            
        }

        objectContext.commitChanges()
    }
    
    
    private Object normalizeValue(Field f) {
        Object result = null
        if (StringUtils.trimToNull(f.value)) {
            switch (f.dataType) {
                case STRING:
                    result = f.value
                    break
                case BOOLEAN:
                    result = Boolean.valueOf(f.value)
                    break
                case DATE:
                case DATETIME:
                    try {
                        result = Date.parse(FormatUtils.DATE_FIELD_PARSE_FORMAT, f.value)
                    } catch (ParseException e) {
                        result = null
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Enter your ${f.name} in the form DD/MM/YYYY")
                    }
                    break
                case INTEGER:
                    result = Integer.valueOf(f.value)
                    break
                case COUNTRY:
                    result = ObjectSelect.query(Country).where(Country.NAME.eq(f.value)).selectFirst(objectContext)
                    if (!result) {
                        errors.fieldsErrors << new FieldError(name: f.key, error: "Country name ${f.value} is incorrect")
                    }
                    break
                case LANGUAGE:
                    result = ObjectSelect.query(Language).where(Language.NAME.eq(f.value)).selectFirst(objectContext)
                    break
                case ENUM:
                    result = TypesUtil.getEnumForDatabaseValue(f.value, this.class.classLoader.loadClass(f.enumType))
                    break
                default:
                    result = null
                    logger.error("unsupported type for field value: $f")
                    errors.formErrors << "unsupported type for field value: ${f.name}".toString()
            }

        } else if (f.mandatory) {
            logger.error("${f.name} required: ${f}")
            errors.fieldsErrors << new FieldError(name: f.key, error: "${f.name} required")
        } else if (COUNTRY == f.dataType) {
            result = ObjectSelect.query(Country).where(Country.NAME.eq(CommonContactValidator.DEFAULT_COUNTRY_NAME)).selectFirst(objectContext)
        }

        result
    }

    private FieldError validateValue(FieldProperty  property, Object value) {
        
        switch (property) {
            case FieldProperty.STREET:
                if ((value as String).length() > street.length) {
                    return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, street.length))
                }
                break
            case FieldProperty.SUBURB:
                if (isDefaultCountry && (value as String).split("\\d").length != 1) {
                    return new FieldError(name: property.key, error: 'A suburb name cannot contain numeric digits.')
                }
                break
            case FieldProperty.POSTCODE:
                if ((value as String).length() > postcode.length) {
                    return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, postcode.length))
                } else if (isDefaultCountry && !(value as String).matches("(\\d){4}")) {
                    return new FieldError(name: property.key, error: 'Enter 4 digit postcode for Australian postcodes.')
                }
                break
            case FieldProperty.STATE:
                if ((value as String).length() > state.length) {
                    return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, state.length))
                }
                break
            case FieldProperty.HOME_PHONE_NUMBER:
            case FieldProperty.BUSINESS_PHONE_NUMBER:
            case FieldProperty.FAX_NUMBER:
                if (isDefaultCountry) {
                    PhoneValidator.Validator validator = PhoneValidator.Validator.valueOf(value as String, property.displayName).validate()
                    value = validator.value
                    if (validator.message) {
                        return new FieldError(name: property.key, error: validator.message)
                    }
                } else {
                    if ((value as String).length() > 20) {
                        return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, 20))
                    }
                }
                break
            case FieldProperty.MOBILE_PHONE_NUMBER:
                if (isDefaultCountry) {
                    PhoneValidator.MobileValidator validator = PhoneValidator.MobileValidator.valueOf(value as String).validate()
                    value = validator.value
                    if (validator.message) {
                        return new FieldError(name: property.key, error: validator.message)
                    } 
                } else {
                    if ((value as String).length() > mobilePhone.length) {
                        return new FieldError(name: property.key, error: String.format(CommonContactValidator.INCORRECT_PROPERTY_LENGTH, property.displayName, mobilePhone.length))
                    }
                }
                break
            case FieldProperty.DATE_OF_BIRTH:
                Date birthDateTruncated = DateUtils.truncate(value as Date, Calendar.DAY_OF_MONTH)
                Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
                if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
                    return new FieldError(name: property.key, error:  "${property.displayName} can not be infuture")
                } else if (CommonContactValidator.MIN_DATE_OF_BIRTH.compareTo(value as Date) > 0){
                    return new FieldError(name: property.key, error:  'Only date of birth in format DD/MM/YYYY and after 01/01/1900 are valid')
                }
                break
            case FieldProperty.YEAR_SCHOOL_COMPLETED:
                break
            default: 
                return null
        }
        null
    }
}
