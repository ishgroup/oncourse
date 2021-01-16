package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.*
import ish.oncourse.services.tag.GetRequirementForType
import ish.oncourse.services.tag.GetTagByPath
import ish.oncourse.services.tag.LinkTagToQueueable
import ish.oncourse.util.contact.CommonContactValidator
import ish.oncourse.willow.functions.field.FieldValueParser
import ish.oncourse.willow.functions.field.FieldValueValidator
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static ish.oncourse.common.field.PropertyGetSetFactory.*
import static ish.oncourse.willow.functions.field.FieldHelper.getContext
import static ish.oncourse.willow.model.field.DataType.TAGGROUP_M
import static ish.oncourse.willow.model.field.DataType.TAGGROUP_S

@CompileStatic
class SubmitContactFields {

    final static Logger logger = LoggerFactory.getLogger(SubmitContactFields.class)
    
    ValidationError errors 
    ObjectContext objectContext
    College college
    WebSite webSite
    Contact contact
    
    private PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
    boolean isDefaultCountry = false
    private String postcodeAutoFill
    private String stateAutoFill


    SubmitContactFields submitContactFields(Contact contact, List<Field> fields) {

        Field country = fields.find { FieldProperty.COUNTRY.key == it.key }
        if (!country || !country.value || CommonContactValidator.DEFAULT_COUNTRY_NAME == country.value) {
            isDefaultCountry = true
        }
        
        fields.each { f ->
            if (isTagProperty(f)) {
                applyTagsField(f)
            } else {
                applyContactField(f)
            }
        }

        setAutoFillValues(FieldProperty.STATE, stateAutoFill, contact)
        setAutoFillValues(FieldProperty.POSTCODE, postcodeAutoFill, contact)
        
        if (contact.getCountry() == null) {
            contact.setCountry(FieldValueParser.getCountryBy(CommonContactValidator.DEFAULT_COUNTRY_NAME, objectContext))
        }
        
        this
    }

    private void applyContactField(Field f) {
        
        FieldProperty  property = FieldProperty.getByKey(f.key)
        FieldValueParser.ParseResult parseResult = new FieldValueParser(f, objectContext).parse() 
        Object value = parseResult.value
        
        if (parseResult.error) {
            errors.formErrors << parseResult.error
        }
        if (parseResult.fieldError) {
            errors.fieldsErrors << parseResult.fieldError
        }
        if (parseResult.postcodeAutoFill) {
            postcodeAutoFill = parseResult.postcodeAutoFill
        }
        if (parseResult.stateAutoFill) {
            stateAutoFill = parseResult.stateAutoFill
        }
        
        if (value != null) {
            if (!property) {
                logger.error "unsupported property ${f.name}".toString()
                errors.formErrors << "unsupported property ${f.name}".toString()
                return
            }

            FieldError error = new FieldValueValidator(property, property.key, objectContext, college, isDefaultCountry).validate(value)
            if (error) {
                errors.fieldsErrors << error
            } else {
                PropertyGetSet getSet = factory.get([getProperty: {f.key}] as FieldInterface, getContext.call(property.contextType, contact))
                getSet.set(value)
            }
        }
    }

    private boolean isTagProperty(Field f) {
        f.key.startsWith(TAG_S_PATTERN) || f.key.startsWith(TAG_M_PATTERN)
    }

    private String getRootTagName(Field f) {
        f.key.replace(TAG_PATTERN, StringUtils.EMPTY)
    }

    private void applyTagsField(Field f) {
        if (f.value) {

            String tagPath = null

            switch (f.dataType) {
                case TAGGROUP_M:
                    tagPath = f.key.replace(TAG_M_PATTERN, StringUtils.EMPTY)
                    break
                case TAGGROUP_S:
                    tagPath = f.value
                    break
            }

            if (tagPath) {
                Tag tag = GetTagByPath.valueOf(contact.objectContext, webSite, tagPath).get()
                if (tag) {
                    LinkTagToQueueable.valueOf(contact.objectContext, contact, tag).apply()
                } else {
                    logger.error "Contact willowId:${contact.id} tried to apply tag [${tagPath}] but tag not found.".toString()
                }
            } else {
                logger.error "Contact willowId:${contact.id} tried to apply tag but tag path is not found.".toString()
            }
        }
    }

    private List<FieldError> validateTagGroup(String rootTagName, List<String> selectedTags) {
        List<FieldError> errors = new ArrayList<>()

        Tag rootTag = GetTagByPath
                .valueOf(contact.objectContext, webSite, rootTagName)
                .get()

        if (rootTag) {
            TagGroupRequirement rootReq = GetRequirementForType.valueOf(rootTag, Contact.class.simpleName).get()
            if (rootReq) {
                if (selectedTags == null || selectedTags.isEmpty()) {
                    errors << new FieldError(name: rootTagName, error: "No one value selected.")
                } else if (!rootReq.allowsMultipleTags && selectedTags.size() > 1) {
                    errors << new FieldError(name: rootTagName, error: "Only one value should be selected.")
                }
            } else {
                logger.error "Contact willowId:${contact.id} tried to apply tags [${StringUtils.join(selectedTags,',')}] but tag group requirement not found.".toString()
            }
        } else {
            logger.error "Contact willowId:${contact.id} tried to apply tags [${StringUtils.join(selectedTags,',')}] but root tag not found.".toString()
        }
        errors
    }

    private setAutoFillValues(FieldProperty property, String value, Contact contact) {
        PropertyGetSet getSet = factory.get([getProperty: {property.key}] as FieldInterface, contact)

        if (getSet.get() == null && value) {
            FieldError error = new FieldValueValidator(property, property.key, objectContext, college, isDefaultCountry).validate(value)

            if (error) {
                errors.fieldsErrors << error
            } else {
                getSet.set(value)
            }
        }
    }

    
}
