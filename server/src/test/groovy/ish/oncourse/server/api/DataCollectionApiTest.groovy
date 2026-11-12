package ish.oncourse.server.api

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.DataType
import ish.common.types.DeliverySchedule
import ish.oncourse.server.api.v1.function.DataCollectionFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.impl.DataCollectionApiImpl
import ish.oncourse.server.cayenne.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import javax.ws.rs.ClientErrorException

@CompileStatic
@DatabaseSetup()
class DataCollectionApiTest extends TestWithDatabase {

   
    @Test
    void testFieldType() {

        CustomFieldType contactField = cayenneContext.newObject(CustomFieldType)
        contactField.entityIdentifier = Contact.class.simpleName
        contactField.name = 'Passport number'
        contactField.key = 'passportNumber'
        contactField.isMandatory = false
        contactField.sortOrder = 0
        contactField.dataType = DataType.TEXT


        CustomFieldType enrolmentField = cayenneContext.newObject(CustomFieldType)
        enrolmentField.entityIdentifier = Enrolment.class.simpleName
        enrolmentField.name = 'Enrolment number'
        enrolmentField.key = 'enrolmentNumber'
        enrolmentField.isMandatory = false
        enrolmentField.sortOrder = 1
        enrolmentField.dataType = DataType.TEXT


        CustomFieldType applicationField = cayenneContext.newObject(CustomFieldType)
        applicationField.entityIdentifier = Application.class.simpleName
        applicationField.name = 'Application number'
        applicationField.key = 'applicationNumber'
        applicationField.isMandatory = false
        applicationField.sortOrder = 2
        applicationField.dataType = DataType.TEXT

        cayenneContext.commitChanges()

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.setCayenneService(cayenneService)
        List<FieldTypeDTO> fieldTypes = integrationApi.getFieldTypes(DataCollectionTypeDTO.ENROLMENT.toString())

        Assertions.assertEquals(33, fieldTypes.size())
        Assertions.assertNotNull(fieldTypes.find { it.uniqueKey == 'customField.contact.passportNumber' })
        Assertions.assertNotNull(fieldTypes.find { it.uniqueKey == 'customField.enrolment.enrolmentNumber' })

        fieldTypes = integrationApi.getFieldTypes(DataCollectionTypeDTO.APPLICATION.toString())

        Assertions.assertEquals(32, fieldTypes.size())
        Assertions.assertNotNull(fieldTypes.find { it.uniqueKey == 'customField.contact.passportNumber' })
        Assertions.assertNotNull(fieldTypes.find { it.uniqueKey == 'customField.application.applicationNumber' })
    }

    @Test
    void testForm() {
        createRule()
        cayenneContext.commitChanges()

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        Assertions.assertEquals(1, integrationApi.rules.size())

        DataCollectionRuleDTO rule = integrationApi.rules[0]

        Assertions.assertEquals('Def rule', rule.name)
        Assertions.assertEquals('Enrol form', rule.enrolmentFormName)
        Assertions.assertEquals('Application form', rule.applicationFormName)
        Assertions.assertEquals('WaitingList form', rule.waitingListFormName)
        Assertions.assertArrayEquals(['Survey form MIDWAY', 'Survey form ON_ENROL'].toArray(),
                rule.surveyForms.sort().toArray())

        List<DataCollectionFormDTO> forms = integrationApi.forms

        Assertions.assertEquals(8, forms.size())

        Assertions.assertEquals(new File(getClass().getResource('/ish/oncourse/server/api/DataCollectionForms.txt')
                .toURI()).text,
                forms.sort { it.name }.toString()
                        .replaceAll(/\n.+id: .+\n/, '\n')
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
            it.tutorDeliverySchedule = DeliveryScheduleTypeDTO.AT_COMPLETION
            it.headings = [new HeadingDTO(name: 'Heading_1', description: 'Heading', fields: [createField('suburb'), createField('state')]),
                           new HeadingDTO(name: 'Heading_1', description: 'Heading', fields: [createField('suburb'), createField('postcode')])]
            it.fields = [createField('street'), createField('postcode')]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        try {
            integrationApi.createForm(form)
            assert false
        } catch (ClientErrorException e) {
            Assertions.assertEquals('Header name should be unique: Heading_1', (e.response.entity as ValidationErrorDTO).errorMessage)
        }

        form = new DataCollectionFormDTO().with {
            it.name = 'form_1'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.tutorDeliverySchedule = DeliveryScheduleTypeDTO.AT_COMPLETION
            it.headings = [new HeadingDTO(name: 'Heading_1', description: 'Heading', fields: [createField('suburb'), createField('state')]),
                           new HeadingDTO(name: 'Heading_2', description: 'Heading', fields: [createField('suburb'), createField('postcode')])]
            it.fields = [createField('street'), createField('postcode')]
            it
        }

        try {
            integrationApi.createForm(form)
            assert false
        } catch (ClientErrorException e) {
            Assertions.assertEquals('Field duplication found for type: postcode, suburb', (e.response.entity as ValidationErrorDTO).errorMessage)
        }

        form = new DataCollectionFormDTO().with {
            it.name = 'form_1'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.tutorDeliverySchedule = DeliveryScheduleTypeDTO.AT_COMPLETION
            it.headings = [new HeadingDTO(name: 'Heading_1', description: 'Heading', fields: [createField('netPromoterScore'), createField('courseScore')]),
                           new HeadingDTO(name: 'Heading_2', description: 'Heading', fields: [createField('venueScore'), createField('tutorScore')])]
            it.fields = [createField('comment')]
            it
        }

        form.headings[0].fields[0].isVisibleToTutor = false
        form.headings[0].fields[1].isVisibleToTutor = true
        form.headings[1].fields[0].isVisibleToTutor = false
        form.headings[1].fields[1].isVisibleToTutor = false
        form.fields[0].isVisibleToTutor = true

        integrationApi.createForm(form)
        FieldConfiguration fieldConfiguration = DataCollectionFunctions.getFormByName(cayenneContext, 'form_1')
        Assertions.assertEquals(5, fieldConfiguration.fields.size())
        Assertions.assertEquals(2, fieldConfiguration.fieldHeadings.size())
        Assertions.assertEquals('[netPromoterScore, courseScore, venueScore, tutorScore, comment]', fieldConfiguration.fields.sort().collect { it.name }.toString())
        Assertions.assertEquals('[false, true, false, false, true]', fieldConfiguration.fields.sort().collect { it.isVisibleToTutor }.toString())
        Assertions.assertEquals(DeliverySchedule.MIDWAY, (fieldConfiguration as SurveyFieldConfiguration).deliverySchedule)
        Assertions.assertEquals(DeliverySchedule.AT_COMPLETION, (fieldConfiguration as SurveyFieldConfiguration).tutorDeliverySchedule)

        DataCollectionFormDTO restForm = DataCollectionFunctions.toRestForm(fieldConfiguration)
        Assertions.assertEquals(DeliveryScheduleTypeDTO.AT_COMPLETION, restForm.tutorDeliverySchedule)
        Assertions.assertTrue(restForm.fields[0].isVisibleToTutor)
        Assertions.assertTrue(restForm.headings[0].fields[1].isVisibleToTutor)
        Assertions.assertFalse(restForm.headings[1].fields[0].isVisibleToTutor)
    }

    @Test
    void testSurveyFormDefaultTutorDeliverySchedule() {
        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_tutor_schedule'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.ON_START
            it.headings = []
            it.fields = [createField('comment')]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        integrationApi.createForm(form)

        SurveyFieldConfiguration fieldConfiguration = DataCollectionFunctions.getFormByName(cayenneContext, 'form_tutor_schedule') as SurveyFieldConfiguration
        Assertions.assertEquals(DeliverySchedule.ON_START, fieldConfiguration.deliverySchedule)
        Assertions.assertEquals(DeliverySchedule.AT_COMPLETION, fieldConfiguration.tutorDeliverySchedule)
        Assertions.assertFalse(fieldConfiguration.fields[0].isVisibleToTutor)
    }

    @Test
    void testTutorVisibilityIgnoredForNonSurveyForm() {
        FieldDTO field = createField('street')
        field.isVisibleToTutor = true

        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_enrolment_visibility'
            it.type = DataCollectionTypeDTO.ENROLMENT
            it.headings = []
            it.fields = [field]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        integrationApi.createForm(form)

        FieldConfiguration fieldConfiguration = DataCollectionFunctions.getFormByName(cayenneContext, 'form_enrolment_visibility')
        Assertions.assertFalse(fieldConfiguration.fields[0].isVisibleToTutor)
    }

    @Test
    void testDefaultTutorVisibility() {
        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_tutor_visibility_default'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.ON_START
            it.headings = [new HeadingDTO(name: 'Scores', description: 'Scores',
                    fields: [createField('netPromoterScore'), createField('courseScore'), createField('venueScore'), createField('tutorScore')])]
            it.fields = [createField('comment')]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        integrationApi.createForm(form)

        // scores are visible to tutors by default, comment and custom fields are hidden
        FieldConfiguration fieldConfiguration = DataCollectionFunctions.getFormByName(cayenneContext, 'form_tutor_visibility_default')
        Map<String, Boolean> visibility = fieldConfiguration.fields.collectEntries { Field f -> [(f.property): f.isVisibleToTutor] }
        Assertions.assertEquals([netPromoterScore: true, courseScore: true, venueScore: true, tutorScore: true, comment: false], visibility)
    }

    @Test
    void testUpdateKeepsTutorSettings() {
        FieldDTO comment = createField('comment')
        comment.isVisibleToTutor = true
        FieldDTO courseScore = createField('courseScore')
        courseScore.isVisibleToTutor = false

        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_tutor_settings_update'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.ON_START
            it.tutorDeliverySchedule = DeliveryScheduleTypeDTO.MIDWAY
            it.headings = []
            it.fields = [comment, courseScore]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        integrationApi.createForm(form)
        FieldConfiguration created = DataCollectionFunctions.getFormByName(cayenneContext, 'form_tutor_settings_update')

        // a client which doesn't know the settings: no tutorDeliverySchedule, no isVisibleToTutor, plus a new field
        DataCollectionFormDTO update = new DataCollectionFormDTO().with {
            it.name = 'form_tutor_settings_update'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.ON_START
            it.headings = []
            it.fields = [createField('comment'), createField('courseScore'), createField('venueScore')]
            it
        }
        integrationApi.updateForm(created.id.toString(), update)

        SurveyFieldConfiguration updated = DataCollectionFunctions.getFormByName(cayenneService.newContext, 'form_tutor_settings_update') as SurveyFieldConfiguration
        Assertions.assertEquals(DeliverySchedule.MIDWAY, updated.tutorDeliverySchedule)
        Map<String, Boolean> visibility = updated.fields.collectEntries { Field f -> [(f.property): f.isVisibleToTutor] }
        Assertions.assertEquals([comment: true, courseScore: false, venueScore: true], visibility)
    }

    @Test
    void testOnDemandTutorDeliveryScheduleRejected() {
        DataCollectionFormDTO form = new DataCollectionFormDTO().with {
            it.name = 'form_tutor_on_demand'
            it.type = DataCollectionTypeDTO.SURVEY
            it.deliverySchedule = DeliveryScheduleTypeDTO.ON_DEMAND
            it.tutorDeliverySchedule = DeliveryScheduleTypeDTO.ON_DEMAND
            it.headings = []
            it.fields = [createField('comment')]
            it
        }

        DataCollectionApiImpl integrationApi = new DataCollectionApiImpl()
        integrationApi.cayenneService = cayenneService
        try {
            integrationApi.createForm(form)
            assert false
        } catch (ClientErrorException e) {
            Assertions.assertEquals("On demand delivery isn't available for tutors", (e.response.entity as ValidationErrorDTO).errorMessage)
        }
    }

    private static FieldDTO createField(String prop) {
        FieldDTO field = new FieldDTO()
        field.type = new FieldTypeDTO(uniqueKey: prop)
        field.label = prop
        field.helpText = prop
        field.mandatory = true
        field
    }

    private void createRule() {
        FieldConfigurationScheme rule = cayenneContext.newObject(FieldConfigurationScheme)
        rule.name = 'Def rule'
        
        FieldConfigurationLink link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('Enrol form', EnrolmentFieldConfiguration)

        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('Application form', ApplicationFieldConfiguration)

        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('WaitingList form', WaitingListFieldConfiguration)
    
        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createSurveyForm('Survey form MIDWAY', DeliverySchedule.MIDWAY)
    
        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createSurveyForm('Survey form ON_ENROL', DeliverySchedule.ON_ENROL)

        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('Article form', ArticleFieldConfiguration)

        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('Membership form', MembershipFieldConfiguration)

        link = cayenneContext.newObject(FieldConfigurationLink)
        link.fieldConfigurationScheme = rule
        link.fieldConfiguration = createForm('Voucher form', VoucherFieldConfiguration)
    
    }

    private  SurveyFieldConfiguration createSurveyForm( String name, DeliverySchedule deliverySchedule) {
        SurveyFieldConfiguration form = cayenneContext.newObject(SurveyFieldConfiguration)
        form.name = name
        
        FieldHeading heading = cayenneContext.newObject(FieldHeading)
        heading.name = 'Enrol heading 1'
        heading.description = 'Enrol heading 1'
        heading.fieldOrder = 1
        
        Field field = cayenneContext.newObject(Field)
        field.name = 'Course score'
        field.property = 'courseScore'
        field.mandatory = true
        field.isVisibleToTutor = true
        field.description = 'Course score'
        field.order = 2
        field.fieldConfiguration = form
        
        heading.addToFields(field)


        field = cayenneContext.newObject(Field)
        field.name = 'Net promoter score'
        field.property = 'netPromoterScore'
        field.mandatory = true
        field.description = 'Net promoter score'
        field.order = 3
        field.fieldConfiguration = form
                 
        heading.addToFields(field)

        field = cayenneContext.newObject(Field)
        field.name = 'Venue score'
        field.property = 'venueScore'
        field.mandatory = true
        field.description = 'Venue score'
        field.order = 4
        field.fieldConfiguration = form
                    
        form.deliverySchedule = deliverySchedule
        form.tutorDeliverySchedule = DeliverySchedule.AT_COMPLETION
        form.addToFieldHeadings(heading)
        
        return form
    }

    private FieldConfiguration createForm(String name, Class<? extends FieldConfiguration> clazz) {
        
        FieldConfiguration form = cayenneContext.newObject(clazz)
        form.name = name
        
        FieldHeading  heading =  cayenneContext.newObject(FieldHeading)
        heading.name = 'Enrol heading 1'
        heading.description = 'Enrol heading 1'
        heading.fieldOrder = 1
        
        Field field = cayenneContext.newObject(Field)
        field.name = 'Postcode'
        field.property = 'postcode'
        field.mandatory = true
        field.description = 'Postcode'
        field.order = 2
        field.fieldConfiguration = form

        heading.addToFields(field)

        field = cayenneContext.newObject(Field)
        field.name = 'State'
        field.property = 'state'
        field.mandatory = true
        field.description = 'State'
        field.order = 3
        field.fieldConfiguration = form
                    
        heading.addToFields(field)
        
        form.addToFieldHeadings(heading)

        heading = cayenneContext.newObject(FieldHeading)
        heading.name = 'Enrol heading 2'
        heading.description = 'Enrol heading 2'
        heading.fieldOrder = 4
        
        field = cayenneContext.newObject(Field)
        field.name = 'Country'
        field.property = 'country'
        field.mandatory = true
        field.description = 'Country'
        field.order = 5
        field.fieldConfiguration = form
        
        heading.addToFields(field)

        field = cayenneContext.newObject(Field)
        field.name = 'Suburb'
        field.property = 'suburb'
        field.mandatory = true
        field.description = 'suburb'
        field.order = 6
        field.fieldConfiguration = form
        heading.addToFields(field)
        form.addToFieldHeadings(heading)


        field = cayenneContext.newObject(Field)
        field.name = 'Street'
        field.property = 'street'
        field.mandatory = true
        field.description = 'Street'
        field.order = 7
        field.fieldConfiguration = form
        form.addToFields(field)

        field = cayenneContext.newObject(Field)
        field.name = 'Citizenship'
        field.property = 'citizenship'
        field.mandatory = true
        field.description = 'Citizenship'
        field.order = 8
        field.fieldConfiguration = form
        form.addToFields(field)
        
        return form
    }
}