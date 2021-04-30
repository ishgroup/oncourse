package ish.oncourse.server.api

import ish.CayenneIshTestCase
import ish.common.types.DataType
import ish.common.types.DeliverySchedule
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.function.DataCollectionFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.impl.DataCollectionApiImpl
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Test
import org.testng.annotations.BeforeTest

import javax.ws.rs.ClientErrorException

import static org.junit.Assert.*

class DataCollectionApiTest  extends CayenneIshTestCase {

    @BeforeTest
    void before() {
        wipeTables()
    }

    @Test
    void testFieldType() {

        ICayenneService cayenneService = injector.getInstance(ICayenneService)

        ObjectContext context = cayenneService.newContext

        CustomFieldType contactField = context.newObject(CustomFieldType)
        contactField.entityIdentifier = Contact.class.simpleName
        contactField.name = 'Passport number'
        contactField.key = 'passportNumber'
        contactField.isMandatory = false
        contactField.sortOrder = 0
        contactField.dataType = DataType.TEXT


        CustomFieldType enrolmentField = context.newObject(CustomFieldType)
        enrolmentField.entityIdentifier = Enrolment.class.simpleName
        enrolmentField.name = 'Enrolment number'
        enrolmentField.key = 'enrolmentNumber'
        enrolmentField.isMandatory = false
        enrolmentField.sortOrder = 1
        enrolmentField.dataType = DataType.TEXT


        CustomFieldType applicationField = context.newObject(CustomFieldType)
        applicationField.entityIdentifier = Application.class.simpleName
        applicationField.name = 'Application number'
        applicationField.key = 'applicationNumber'
        applicationField.isMandatory = false
        applicationField.sortOrder = 2
        applicationField.dataType = DataType.TEXT

        context.commitChanges()

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService

        List<FieldTypeDTO> fieldTypes = integrationApi.getFieldTypes(DataCollectionTypeDTO.ENROLMENT.toString())

        assertEquals(32,fieldTypes.size())
        assertNotNull(fieldTypes.find {it.uniqueKey == 'customField.contact.passportNumber'})
        assertNotNull(fieldTypes.find {it.uniqueKey == 'customField.enrolment.enrolmentNumber'})

        fieldTypes = integrationApi.getFieldTypes(DataCollectionTypeDTO.APPLICATION.toString())

        assertEquals(32,fieldTypes.size())
        assertNotNull(fieldTypes.find {it.uniqueKey == 'customField.contact.passportNumber'})
        assertNotNull(fieldTypes.find {it.uniqueKey == 'customField.application.applicationNumber'})

    }


    @Test
    void testForm() {

        ICayenneService cayenneService = injector.getInstance(ICayenneService)
        ObjectContext context = cayenneService.newContext
        createRule(context)
        context.commitChanges()

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        assertEquals(1, integrationApi.rules.size())

        DataCollectionRuleDTO rule = integrationApi.rules[0]

        assertEquals('Def rule', rule.name)
        assertEquals('Enrol form', rule.enrolmentFormName)
        assertEquals('Application form', rule.applicationFormName)
        assertEquals('WaitingList form', rule.waitingListFormName)
        assertArrayEquals(['Survey form MIDWAY', 'Survey form ON_ENROL'].toArray(),
                rule.surveyForms.sort().toArray())

        List<DataCollectionFormDTO> forms = integrationApi.forms

        assertEquals(5, forms.size())

        assertEquals(new File(getClass().getResource('/ish/oncourse/server/api/DataCollectionForms.txt')
                .toURI()).text,
                forms.sort{it.name}.toString()
                        .replaceAll(/id: .+\n/, '\n')
                        .replaceAll(/.+created: .+\n/, '')
                        .replaceAll(/.+modified: .+\n/, '')

        )
    }


    @Test
    void testCreateForm() {

        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_1'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.headings = [new HeadingDTO(name: 'Heading_1',description: 'Heading', fields: [createField('suburb'), createField('state')]),
                           new HeadingDTO(name: 'Heading_1',description: 'Heading', fields: [createField('suburb'), createField('postcode')])]
            it.fields = [createField('street'),createField('postcode')]
            it
        }

        ICayenneService cayenneService = injector.getInstance(ICayenneService)

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        try {
            integrationApi.createForm(form)
            assert false
        } catch (ClientErrorException e) {
           assertEquals('Header name should be unique: Heading_1', (e.response.entity as ValidationErrorDTO).errorMessage)
        }

        form = new DataCollectionFormDTO().with {
            it.name = 'form_1'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.headings = [new HeadingDTO(name: 'Heading_1',description: 'Heading', fields: [createField('suburb'), createField('state')]),
                           new HeadingDTO(name: 'Heading_2',description: 'Heading', fields: [createField('suburb'), createField('postcode')])]
            it.fields = [createField('street'),createField('postcode')]
            it
        }

        try {
            integrationApi.createForm(form)
            assert false
        } catch (ClientErrorException e) {
            assertEquals('Field duplication found for type: postcode, suburb', (e.response.entity as ValidationErrorDTO).errorMessage)
        }

        form = new DataCollectionFormDTO().with {
            it.name = 'form_1'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.headings = [new HeadingDTO(name: 'Heading_1',description: 'Heading', fields: [createField('netPromoterScore'), createField('courseScore')]),
                           new HeadingDTO(name: 'Heading_2',description: 'Heading', fields: [createField('venueScore'), createField('tutorScore')])]
            it.fields = [createField('comment')]
            it
        }

        integrationApi.createForm(form)
        FieldConfiguration fieldConfiguration = DataCollectionFunctions.getFormByName(cayenneService.newContext,'form_1')
        assertEquals(5, fieldConfiguration.fields.size())
        assertEquals(2, fieldConfiguration.fieldHeadings.size())
        assertEquals('[netPromoterScore, courseScore, venueScore, tutorScore, comment]', fieldConfiguration.fields.sort().collect {it.name}.toString())

    }

    private static FieldDTO createField(String prop) {
        FieldDTO field = new FieldDTO()
        field.type = new FieldTypeDTO(uniqueKey: prop)
        field.label = prop
        field.helpText = prop
        field.mandatory = true
        field
    }

    private static void createRule(ObjectContext context) {
        FieldConfigurationScheme rule =  context.newObject(FieldConfigurationScheme)
        rule.name = 'Def rule'
        context.newObject(FieldConfigurationLink).with { link ->
            link.fieldConfigurationScheme = rule
            link.fieldConfiguration = cretaeForm(context, 'Enrol form', EnrolmentFieldConfiguration)
        }
        context.newObject(FieldConfigurationLink).with { link ->
            link.fieldConfigurationScheme = rule
            link.fieldConfiguration = cretaeForm(context, 'Application form', ApplicationFieldConfiguration)
        }
        context.newObject(FieldConfigurationLink).with { link ->
            link.fieldConfigurationScheme = rule
            link.fieldConfiguration = cretaeForm(context, 'WaitingList form', WaitingListFieldConfiguration)
        }
        context.newObject(FieldConfigurationLink).with { link ->
            link.fieldConfigurationScheme = rule
            link.fieldConfiguration = cretaeSurveyForm(context, 'Survey form MIDWAY', DeliverySchedule.MIDWAY)
        }
        context.newObject(FieldConfigurationLink).with { link ->
            link.fieldConfigurationScheme = rule
            link.fieldConfiguration = cretaeSurveyForm(context, 'Survey form ON_ENROL', DeliverySchedule.ON_ENROL)
        }
    }

    private static SurveyFieldConfiguration cretaeSurveyForm(ObjectContext context, String name, DeliverySchedule deliverySchedule) {
        return context.newObject(SurveyFieldConfiguration).with { form ->
            form.name = name
            form.addToFieldHeadings  context.newObject(FieldHeading).with { heading ->
                heading.name = 'Enrol heading 1'
                heading.description = 'Enrol heading 1'
                heading.fieldOrder = 1
                heading.addToFields context.newObject(Field).with { field ->
                    field.name = 'Course score'
                    field.property = 'courseScore'
                    field.mandatory = true
                    field.description = 'Course score'
                    field.order = 2
                    field.fieldConfiguration = form
                    field
                } as Field
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'Net promoter score'
                    field.property = 'netPromoterScore'
                    field.mandatory = true
                    field.description = 'Net promoter score'
                    field.order = 3
                    field.fieldConfiguration = form
                    field
                }
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'Venue score'
                    field.property = 'venueScore'
                    field.mandatory = true
                    field.description = 'Venue score'
                    field.order = 4
                    field.fieldConfiguration = form
                    field
                }
                heading
            }
            form.deliverySchedule = deliverySchedule

            form
        }
    }

    private static FieldConfiguration cretaeForm(ObjectContext context, String name, Class<? extends FieldConfiguration> clazz) {
        return context.newObject(clazz).with { form ->
            form.name = name
            form.addToFieldHeadings  context.newObject(FieldHeading).with { heading ->
                heading.name = 'Enrol heading 1'
                heading.description = 'Enrol heading 1'
                heading.fieldOrder = 1
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'Postcode'
                    field.property = 'postcode'
                    field.mandatory = true
                    field.description = 'Postcode'
                    field.order = 2
                    field.fieldConfiguration = form
                    field
                }
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'State'
                    field.property = 'state'
                    field.mandatory = true
                    field.description = 'State'
                    field.order = 3
                    field.fieldConfiguration = form
                    field
                }
                heading
            }
            form.addToFieldHeadings context.newObject(FieldHeading).with { heading ->
                heading.name = 'Enrol heading 2'
                heading.description = 'Enrol heading 2'
                heading.fieldOrder = 4
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'Country'
                    field.property = 'country'
                    field.mandatory = true
                    field.description = 'Country'
                    field.order = 5
                    field.fieldConfiguration = form
                    field
                }
                heading.addToFields context.newObject(Field).with {field ->
                    field.name = 'Suburb'
                    field.property = 'suburb'
                    field.mandatory = true
                    field.description = 'suburb'
                    field.order = 6
                    field.fieldConfiguration = form
                    field
                }
                heading
            }
            form.addToFields context.newObject(Field).with {field ->
                field.name = 'Street'
                field.property = 'street'
                field.mandatory = true
                field.description = 'Street'
                field.order = 7
                field.fieldConfiguration = form
                field
            }
            form.addToFields context.newObject(Field).with {field ->
                field.name = 'Citizenship'
                field.property = 'citizenship'
                field.mandatory = true
                field.description = 'Citizenship'
                field.order = 8
                field.fieldConfiguration = form
                field
            }
            form
        }
    }
}