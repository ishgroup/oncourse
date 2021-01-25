package ish.oncourse.willow.functions.field

import groovy.transform.CompileStatic
import ish.common.types.TypesUtil
import ish.common.types.YesNoOptions
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.Suburb
import org.apache.cayenne.ExtendedEnumeration
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.ParseException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

import static ish.oncourse.willow.model.field.DataType.*

@CompileStatic
class FieldValueParser {

    public static final String DATE_PATTERN ="yyyy-MM-dd"
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    public static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern(DATE_PATTERN)
            .toFormatter(Locale.ENGLISH)

    public static final DateTimeFormatter  DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern(DATE_TIME_PATTERN)
            .toFormatter(Locale.ENGLISH)
    
    final static Logger logger = LoggerFactory.getLogger(FieldValueParser)

    Field field
    ObjectContext context
    
    
    FieldValueParser(Field field, ObjectContext context) {
        this.field = field
        this.context = context
    }


    ParseResult parse() {
        ParseResult result = new ParseResult()
        if (StringUtils.trimToNull(field.value) || field.itemValue) {

            if (field.key.startsWith(PropertyGetSetFactory.CUSTOM_FIELD_PROPERTY_PATTERN)) {
                result.value = field.value
                return result
            }
            
            switch (field.dataType) {
                case EMAIL:
                case LONG_STRING:
                case POSTCODE:
                case STRING:
                case CHOICE:
                case PHONE:
                    result.value = field.value.trim()
                    break
                case SUBURB:
                    if (StringUtils.trimToNull(field.value)) {
                        result.value = field.value.trim()
                    } else if (field.itemValue) {
                        try {
                            Suburb suburb = field.itemValue.value as Suburb
                            result.postcodeAutoFill = suburb.postcode
                            result.stateAutoFill = suburb.state
                            result.value = suburb.suburb
                        } catch (ClassCastException e){
                            result.fieldError = new FieldError(name: field.key, error: "${field.name} is incorrect")
                        }
                    }
                    break
                case BOOLEAN:
                    result.value = Boolean.valueOf(field.value)
                    break
                case DATE:
                    Date date 
                    try {
                        date =  Date.parse(field.value, DATE_PATTERN)
                    } catch (ParseException ignore){
                        try {
                            date = Date.parse(field.value,FormatUtils.DATE_FIELD_PARSE_FORMAT)
                        } catch (ParseException e) {
                            result.value = null
                            result.fieldError = new FieldError(name: field.key, error: "Enter your ${field.name} in the form DD/MM/YYYY")
                        }
                    }
                    
                    if (date) {
                        result.value = DateUtils.truncate(date, Calendar.DAY_OF_MONTH)
                    }
                    
                    break
                case INTEGER:
                    if (org.apache.commons.lang.StringUtils.isNumeric(field.value)) {
                        result.value = Integer.valueOf(field.value)
                    } else {
                        result.fieldError = new FieldError(name: field.key, error: "Enter numeric value for ${field.name}")
                    }
                    break
                case COUNTRY:
                    result.value = getCountryBy(field.value, context)
                    if (!result) {
                        result.fieldError = new FieldError(name: field.key, error: "Incorrect format of ${field.name}")
                    }
                    break
                case LANGUAGE:
                    result.value = getLanguageBy(field.value, context)
                    if (!result) {
                        result.fieldError = new FieldError(name: field.key, error: "Language name ${field.value} is incorrect")
                    }
                    break
                case ENUM:
                     if (org.apache.commons.lang.StringUtils.isNumeric(field.value)) {
                        result.value = TypesUtil.getEnumForDatabaseValue(field.value, this.class.classLoader.loadClass("ish.common.types.$field.enumType") as Class< ?extends ExtendedEnumeration>)
                        if (FieldProperty.IS_STILL_AT_SCHOOL.key == field.key) {
                            result.value = (result.value as YesNoOptions).booleanValue
                        }
                    } else {
                        result.fieldError = new FieldError(name: field.key, error: "${field.name} is incorrect")
                    }
                    break
                case TAGGROUP_S :
                case TAGGROUP_M :
                    break
                default:
                    result.value = null
                    logger.error("unsupported type for field value: $field")
                    result.error = "unsupported type for field value: ${field.name}".toString()
            }

        } else if (field.mandatory) {
            logger.error("${field.name} required: ${field}")
            result.error = new FieldError(name: field.key, error: "${field.name} is required")
        }
        result
    }

    static Country getCountryBy(String name, ObjectContext context) {
        ObjectSelect.query(Country).where(Country.NAME.eq(name))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(Country.class.simpleName)
                .selectFirst(context)
    }

    static Language getLanguageBy(String name, ObjectContext context) {
        ObjectSelect.query(Language).where(Language.NAME.eq(name))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(Language.class.simpleName)
                .selectFirst(context)
    }
    
    static class ParseResult {
        Object value
        String postcodeAutoFill
        String stateAutoFill
        
        FieldError fieldError 
        String error
    }

}
