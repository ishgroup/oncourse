/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.ArticleFieldConfiguration
import ish.oncourse.server.cayenne.MembershipFieldConfiguration
import ish.oncourse.server.cayenne.VoucherFieldConfiguration
import ish.oncourse.server.cayenne.WaitingList

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.common.types.DataType.LIST
import static ish.common.types.DataType.MAP
import static ish.common.types.DataType.TEXT
import ish.common.types.DeliverySchedule
import ish.common.types.FieldConfigurationType
import ish.common.types.NodeType
import static ish.oncourse.common.field.FieldProperty.*
import static ish.oncourse.common.field.PropertyGetSetFactory.CUSTOM_FIELD_PROPERTY_PATTERN
import static ish.oncourse.common.field.PropertyGetSetFactory.TAG_PATTERN
import ish.oncourse.server.api.v1.model.DataCollectionFormDTO
import ish.oncourse.server.api.v1.model.DataCollectionRuleDTO
import ish.oncourse.server.api.v1.model.DataCollectionTypeDTO
import ish.oncourse.server.api.v1.model.DeliveryScheduleTypeDTO
import ish.oncourse.server.api.v1.model.FieldDTO
import ish.oncourse.server.api.v1.model.FieldTypeDTO
import ish.oncourse.server.api.v1.model.HeadingDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ApplicationFieldConfiguration
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.EnrolmentFieldConfiguration
import ish.oncourse.server.cayenne.Field
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldConfigurationLink
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.FieldHeading
import ish.oncourse.server.cayenne.ParentFieldConfiguration
import ish.oncourse.server.cayenne.PayerFieldConfiguration
import ish.oncourse.server.cayenne.Survey
import ish.oncourse.server.cayenne.SurveyFieldConfiguration
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.WaitingListFieldConfiguration
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

@CompileStatic
class DataCollectionFunctions {

    private static final List<FieldTypeDTO> VISIBLE_FIELDS
    private static final List<FieldTypeDTO> WAITING_LIST_FIELDS
    private static final Map<DataCollectionTypeDTO, Class<? extends FieldConfiguration>> CONFIGURATION_MAP

    static {
            VISIBLE_FIELDS = [ STREET, SUBURB, POSTCODE, STATE, COUNTRY, HOME_PHONE_NUMBER, BUSINESS_PHONE_NUMBER, FAX_NUMBER,
                               MOBILE_PHONE_NUMBER, DATE_OF_BIRTH, ABN, IS_MALE, IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY,
                               IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY, CITIZENSHIP, COUNTRY_OF_BIRTH, LANGUAGE_HOME,
                               YEAR_SCHOOL_COMPLETED, ENGLISH_PROFICIENCY, INDIGENOUS_STATUS, HIGHEST_SCHOOL_LEVEL, IS_STILL_AT_SCHOOL,
                               PRIOR_EDUCATION_CODE, LABOUR_FORCE_STATUS, DISABILITY_TYPE, SPECIAL_NEEDS, MIDDLE_NAME, HONORIFIC, TITLE ]
                                .collect { new FieldTypeDTO(uniqueKey: it.key, label: it.displayName)}
        
            WAITING_LIST_FIELDS = [DETAIL, STUDENTS_COUNT].collect { new FieldTypeDTO(uniqueKey: it.key, label: it.displayName)}
        }

    static {
        CONFIGURATION_MAP = [
                (DataCollectionTypeDTO.ENROLMENT) : EnrolmentFieldConfiguration,
                (DataCollectionTypeDTO.APPLICATION) : ApplicationFieldConfiguration,
                (DataCollectionTypeDTO.WAITINGLIST) : WaitingListFieldConfiguration,
                (DataCollectionTypeDTO.SURVEY) : SurveyFieldConfiguration,
                (DataCollectionTypeDTO.PAYER) : PayerFieldConfiguration,
                (DataCollectionTypeDTO.PARENT) : ParentFieldConfiguration,
                (DataCollectionTypeDTO.PRODUCT) : ArticleFieldConfiguration,
                (DataCollectionTypeDTO.MEMBERSHIP) : MembershipFieldConfiguration,
                (DataCollectionTypeDTO.VOUCHER) : VoucherFieldConfiguration
        ]
    }


    static List<FieldTypeDTO> getFieldTypes(String formType, ObjectContext context) {
        List<FieldTypeDTO> fieldTypes


        if (Survey.simpleName == formType) {
            fieldTypes = ObjectSelect.query(CustomFieldType)
                    .where(CustomFieldType.ENTITY_IDENTIFIER.eq(Survey.class.simpleName))
                    .and(CustomFieldType.DATA_TYPE.in(TEXT, LIST, MAP))
                    .orderBy(CustomFieldType.SORT_ORDER.asc())
                    .select(context)
                    .collect { new FieldTypeDTO(uniqueKey: "${CUSTOM_FIELD_PROPERTY_PATTERN}${it.entityIdentifier.toLowerCase()}.${it.key}", label: it.name) }

            fieldTypes << new FieldTypeDTO(uniqueKey: NET_PROMOTER_SCORE.key, label: NET_PROMOTER_SCORE.displayName)
            fieldTypes << new FieldTypeDTO(uniqueKey: COURSE_SCORE.key, label: COURSE_SCORE.displayName)
            fieldTypes << new FieldTypeDTO(uniqueKey: VENUE_SCORE.key, label: VENUE_SCORE.displayName)
            fieldTypes << new FieldTypeDTO(uniqueKey: TUTOR_SCORE.key, label: TUTOR_SCORE.displayName)
            fieldTypes << new FieldTypeDTO(uniqueKey: COMMENT.key, label: COMMENT.displayName)
        } else {
            fieldTypes = ObjectSelect.query(CustomFieldType)
                    .where(CustomFieldType.ENTITY_IDENTIFIER.in(Contact.class.simpleName, formType))
                    .orderBy(CustomFieldType.SORT_ORDER.asc())
                    .select(context)
                    .collect { new FieldTypeDTO(uniqueKey: "${CUSTOM_FIELD_PROPERTY_PATTERN}${it.entityIdentifier.toLowerCase()}.${it.key}", label: it.name) }

            List<FieldTypeDTO> tagFieldTypes = ObjectSelect.query(Tag.class)
                    .where(Tag.NODE_TYPE.eq(NodeType.TAG))
                    .and(Tag.PARENT_TAG.isNull())
                    .and(Tag.SPECIAL_TYPE.isNull())
                    .select(context)
                    .findAll {t -> t.getTagRequirement(Contact.class)}
                    .collect{new FieldTypeDTO(uniqueKey: "${TAG_PATTERN}${it.pathName}", label: it.name)}

            fieldTypes += VISIBLE_FIELDS
            
            if (WaitingList.simpleName == formType) {
                fieldTypes += WAITING_LIST_FIELDS
            }
            fieldTypes += tagFieldTypes
        }

        return fieldTypes
    }

    static FieldConfiguration getFormByName(ObjectContext context, String name) {
        return ObjectSelect.query(FieldConfiguration).where(FieldConfiguration.NAME.eq(name))
                .selectOne(context)
    }

    static FieldConfiguration getFormById(ObjectContext context, String id) {
        return SelectById.query(FieldConfiguration, id)
                .prefetch(FieldConfiguration.FIELD_CONFIGURATION_LINK.joint())
                .selectOne(context)
    }

    static FieldConfigurationScheme getRuleByName(ObjectContext context, String name) {
        return ObjectSelect.query(FieldConfigurationScheme).where(FieldConfigurationScheme.NAME.eq(name))
                .selectOne(context)
    }

    static FieldConfigurationScheme getRuleById(ObjectContext context, String id) {
        return SelectById.query(FieldConfigurationScheme, id)
                .prefetch(FieldConfigurationScheme.FIELD_CONFIGURATION_LINKS.joint())
                .prefetch(FieldConfigurationScheme.COURSES.joint())
                .selectOne(context)
    }

    static ValidationErrorDTO validateForm(ObjectContext context, DataCollectionFormDTO form, FieldConfiguration  persistForm = null) {

        if (!form.name || form.name.empty) {
            return new ValidationErrorDTO(null, 'name', "Form name can not be empty")
        }

        if (!form.type) {
            return new ValidationErrorDTO(null, 'type', "Form type should be specified")
        }
        FieldConfiguration duplicate = getFormByName(context, form.name)

        if (duplicate && (!persistForm || duplicate.id != persistForm.id)) {
            return new ValidationErrorDTO(null, 'name', "Form name should be unique")
        }

        if (persistForm && persistForm.type.displayName.replace(' ', '') !=  form.type.toString()) {
            return new ValidationErrorDTO(null, 'type', "Form type can not be changed")
        }

        if (form.type == DataCollectionTypeDTO.SURVEY && !form.deliverySchedule) {
            return new ValidationErrorDTO(null, 'deliverySchedule', "Delivery schedule required for Survey form")
        }

        List<String> headerNames =  form.headings*.name.flatten() as List<String>
        List<String> headerDuplicates = headerNames.findAll{headerNames.count(it) > 1}.unique()

        if (!headerDuplicates.empty) {
            return new ValidationErrorDTO(null, 'name', "Header name should be unique: ${headerDuplicates.join(', ')}")
        }

        List<String>  allFields = form.fields*.type*.uniqueKey.flatten() + form.headings*.fields*.type*.uniqueKey.flatten() as List<String>
        List<String> duplicates = allFields.findAll{allFields.count(it) > 1}.unique()

        if (!duplicates.empty) {
            return new ValidationErrorDTO(null, 'uniqueKey', "Field duplication found for type: ${duplicates.join(', ')}")
        }

        List<String> availableTypes =  getFieldTypes(form.type.toString(), context).collect {it.uniqueKey}
        List<String> unavailableTypes =  allFields.findAll {!(it in availableTypes)}

        if (!unavailableTypes.empty) {
            List<FieldDTO> fields = form.fields + (form.headings*.fields.flatten() as List<FieldDTO>)
            String fieldNames =  unavailableTypes.collect { t -> fields.find {f -> f.type.uniqueKey == t}?.label}.join(', ')
            return  new ValidationErrorDTO(null, 'uniqueKey', "Field types: ${fieldNames} are not available for the form")
        }

        return null

    }

    static void validateRule(ObjectContext context, DataCollectionRuleDTO rule, FieldConfigurationScheme persistRule = null) {

        if (!rule.name || rule.name.empty) {
            throwError(new ValidationErrorDTO(null, 'name', "Rule name can not be empty"))
        }

        FieldConfigurationScheme duplicate = getRuleByName(context, rule.name)

        if (duplicate && (!persistRule || duplicate.id != persistRule.id)) {
            throwError(new ValidationErrorDTO(null, 'name', "Rule name should be unique"))
        }

        validateConfiguration(context, rule.enrolmentFormName, 'enrolmentFormName', EnrolmentFieldConfiguration, true)
        validateConfiguration(context, rule.applicationFormName, 'applicationFormName', ApplicationFieldConfiguration, true)
        validateConfiguration(context, rule.waitingListFormName, 'waitingListFormName', WaitingListFieldConfiguration, true)
        validateConfiguration(context, rule.productFormName, 'productFormName', ArticleFieldConfiguration, true)
        validateConfiguration(context, rule.membershipFormName, 'membershipFormName', MembershipFieldConfiguration, true)
        validateConfiguration(context, rule.voucherFormName, 'voucherFormName', VoucherFieldConfiguration, true)

        if (rule.payerFormName) {
            validateConfiguration(context, rule.payerFormName, 'payerFormName', PayerFieldConfiguration, false)
        }

        if (rule.parentFormName) {
            validateConfiguration(context, rule.parentFormName, 'parentFormName', ParentFieldConfiguration, false)
        }

        for (String surveyFormName : rule.surveyForms) {
            validateConfiguration(context, surveyFormName, 'surveyForms', SurveyFieldConfiguration, false)
        }
    }

    static void validateConfiguration(ObjectContext context, String configurationName, String propertyName,
                                                    Class<? extends FieldConfiguration> clzz, boolean isAllowEmpty) {
        if (!isAllowEmpty && (configurationName == null || configurationName.empty)) {
            throwError(new ValidationErrorDTO(null, propertyName, "Form name can not be empty"))
        }

        FieldConfiguration form = getFormByName(context, configurationName)

        if (!form) {
            throwError(new ValidationErrorDTO(null, propertyName, "Form $configurationName is not exist"))
        }

        if (!(form.class == clzz)) {
            throwError(new ValidationErrorDTO(null, propertyName, "Form $configurationName has another type: $form.type.displayName"))
        }
    }

    static void throwError(ValidationErrorDTO error) {
        throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
    }


    static FieldConfigurationScheme toDbRule(ObjectContext context, DataCollectionRuleDTO rule, FieldConfigurationScheme persistRule = null) {
        List<String> forms = [rule.enrolmentFormName, rule.applicationFormName, rule.waitingListFormName, rule.productFormName,
                              rule.membershipFormName, rule.voucherFormName] + rule.surveyForms

        if (rule.payerFormName) {
            forms += rule.payerFormName
        }
        if (rule.parentFormName) {
            forms += rule.parentFormName
        }

        FieldConfigurationScheme ruleToUpdate = persistRule?:context.newObject(FieldConfigurationScheme)

        return ruleToUpdate.with { dbRule ->
            dbRule.name = rule.name
            forms.each { form ->
                context.newObject(FieldConfigurationLink).with { relation ->
                    relation.fieldConfigurationScheme = dbRule
                    relation.fieldConfiguration = getFormByName(context, form)
                }
            }
            dbRule
        }
    }

    static FieldConfiguration toDbForm(ObjectContext context, DataCollectionFormDTO form, FieldConfiguration persistRecord = null) {
        FieldConfiguration formToUpdate = persistRecord?:context.newObject(CONFIGURATION_MAP[form.type] as Class<? extends  FieldConfiguration>)
        return formToUpdate.with { dbForm ->
            int order = 0
            dbForm.name = form.name
            form.headings.each { heading ->
                dbForm.addToFieldHeadings context.newObject(FieldHeading).with { dbHeading ->
                    dbHeading.fieldOrder = order++
                    dbHeading.name = heading.name
                    dbHeading.description = heading.description
                    heading.fields.each { field -> dbHeading.addToFields toDbField(context, dbForm, field, order++)}
                    dbHeading
                }
            }
            form.fields.each { field -> dbForm.addToFields toDbField(context, dbForm, field, order++) }
            if (dbForm instanceof SurveyFieldConfiguration) {
                (dbForm as SurveyFieldConfiguration).deliverySchedule = DeliverySchedule.valueOf(form.deliverySchedule.name())
            }
            dbForm
        }
    }

    static Field toDbField(ObjectContext context, FieldConfiguration dbForm, FieldDTO field, int order) {
        context.newObject(Field).with { dbField ->
            dbField.order = order
            dbField.property = field.type.uniqueKey
            dbField.fieldConfiguration = dbForm
            dbField.name = field.label
            dbField.description = field.helpText
            dbField.mandatory = field.mandatory
            dbField
        }
    }


    static DataCollectionFormDTO toRestForm(FieldConfiguration dbForm) {

        DataCollectionTypeDTO formType = DataCollectionTypeDTO.fromValue(dbForm.type.displayName.replace(' ', ''))
        List<FieldTypeDTO> availableTypes =  getFieldTypes(formType.toString(), dbForm.context)
        new DataCollectionFormDTO().with { form ->
            form.id = dbForm.id.toString()
            form.created =  dbForm.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            form.modified =  dbForm.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            form.name = dbForm.name
            form.type = formType
            form.headings = dbForm.fieldHeadings.sort().collect { toRestHeading(it, availableTypes) }
            form.fields = dbForm.fields.findAll {it.fieldHeading == null}.sort().collect { toRestField(it, availableTypes) }

            if (dbForm instanceof SurveyFieldConfiguration) {
                form.deliverySchedule = DeliveryScheduleTypeDTO.fromValue((dbForm as SurveyFieldConfiguration).deliverySchedule?.displayName)
            }
            form
        }
    }

    static HeadingDTO toRestHeading(FieldHeading dbHeading, List<FieldTypeDTO> availableTypes) {
        new  HeadingDTO().with { header ->
            header.name = dbHeading.name
            header.description = dbHeading.description
            header.fields = dbHeading.fields.sort {it.order}.collect { toRestField(it, availableTypes)}
            header
        }
    }

    static FieldDTO toRestField(Field dbField, List<FieldTypeDTO> availableTypes) {
        return new FieldDTO().with { field ->
            field.type = availableTypes.find{it.uniqueKey == dbField.property}?:new FieldTypeDTO(uniqueKey:dbField.property)
            field.label = dbField.name
            field.helpText = dbField.description
            field.mandatory = dbField.mandatory
            field
        }
    }

    static DataCollectionRuleDTO toRestRule(FieldConfigurationScheme dbRule) {
        return new DataCollectionRuleDTO().with { rule ->
            rule.id = dbRule.id.toString()
            rule.created =  dbRule.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            rule.modified =  dbRule.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            rule.name = dbRule.name
            rule.applicationFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.APPLICATION}.fieldConfiguration.name
            rule.enrolmentFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.ENROLMENT}.fieldConfiguration.name
            rule.waitingListFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.WAITING_LIST}.fieldConfiguration.name
            rule.payerFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.PAYER}?.fieldConfiguration?.name
            rule.parentFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.PARENT}?.fieldConfiguration?.name
            rule.surveyForms = dbRule.fieldConfigurationLinks.findAll {it.fieldConfiguration.type == FieldConfigurationType.SURVEY}.collect {it.fieldConfiguration.name}
            rule.productFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.PRODUCT}.fieldConfiguration.name
            rule.membershipFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.MEMBERSHIP}.fieldConfiguration.name
            rule.voucherFormName = dbRule.fieldConfigurationLinks.find {it.fieldConfiguration.type == FieldConfigurationType.VOUCHER}.fieldConfiguration.name
            rule
        }
    }
}
