package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.FieldHeading
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.request.ProductContainer
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.ContactFieldsRequest
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldSet

import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.junit.*

import javax.ws.rs.BadRequestException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse


class ContactDetailsTest extends  ApiTest{
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTest.xml'
    }
    
    @Test
    void testGet() {

        ObjectContext context = cayenneService.newContext()

        SelectById.query(FieldHeading, 1l).selectOne(context).order = 1
        SelectById.query(FieldHeading, 2l).selectOne(context).order = 2
        SelectById.query(FieldHeading, 3l).selectOne(context).order = 1
        SelectById.query(FieldHeading, 4l).selectOne(context).order = 2
        SelectById.query(FieldHeading, 5l).selectOne(context).order = 3

        context.commitChanges()

        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        ContactFields fields = api.getContactFields(new ContactFieldsRequest(contactId: '1001', products: [[productId:'1001',quantity:1] as ProductContainer ], classIds: ['1001', '1002'], fieldSet: FieldSet.ENROLMENT))
        
        def file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-fields.txt').toURI())

        assertEquals(file.text, fields.toString())

        fields = api.getContactFields(new ContactFieldsRequest(contactId: '1002', classIds: ['1001'], fieldSet: FieldSet.ENROLMENT, mandatoryOnly: true))

        file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-fields-empty.txt').toURI())
        
        assertEquals(file.text, fields.toString())

    }

    @Test
    void testSubmitWrongRequest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        

        try {
            api.submitContactDetails( wrongRequest())
        } catch (BadRequestException e) {
            def file = new File(getClass().getResource('/ish/oncourse/willow/service/validation-error.txt').toURI())
            assertEquals(file.text, e.response.entity.toString() )
            return
        }
        assertFalse(true)
    }

    @Test
    void testSubmitProperRequest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        
        api.submitContactDetails(propperRequest())
        
        Contact contact = SelectById.query(Contact, 1001l).selectOne(cayenneService.newContext())
        assertEquals(contact.street, 'street')
        assertEquals(contact.suburb, 'Parramata')
        assertEquals(contact.postcode, '6797')
        assertEquals(contact.country.name, 'Australia')
        assertEquals(contact.businessPhoneNumber, '02 5551 5678')
        assertEquals(contact.faxNumber, '02 5551 5678')
        assertEquals(contact.mobilePhoneNumber, '0491 570 156')
        assertEquals(contact.dateOfBirth, DateUtils.truncate(Date.parse(FormatUtils.DATE_FIELD_PARSE_FORMAT, '20/07/1991'), Calendar.DAY_OF_MONTH))
        assertEquals(contact.abn,'1234')
        assertEquals(contact.isMale,true)
        assertEquals(contact.isMarketingViaEmailAllowed,false)
        assertEquals(contact.isMarketingViaPostAllowed,true)
        assertEquals(contact.isMarketingViaSMSAllowed,true)
        assertEquals(contact.student.citizenship.databaseValue,2)
        assertEquals(contact.student.countryOfBirth.name,'Australia')
        assertEquals(contact.student.languageHome.name,'English')
        assertEquals(contact.student.yearSchoolCompleted,2009)
        assertEquals(contact.student.indigenousStatus.databaseValue,3)
        assertEquals(contact.student.highestSchoolLevel.databaseValue,6)
        assertEquals(contact.student.isStillAtSchool, true)
        assertEquals(contact.student.priorEducationCode.databaseValue,1)
        assertEquals(contact.student.labourForceStatus.databaseValue,1)
        assertEquals(contact.student.disabilityType.databaseValue,1)
        assertEquals(contact.student.specialNeeds,'special needs')
        assertEquals(contact.getCustomFieldValue('carMaker'),'BMW')
        
    }
    
    private ContactFields wrongRequest() {
        new ContactFields().with {
            it.contactId = '1001'
            List<Field> fields = []
            fields << new Field().with { f ->
                f.key = 'street'
                f.name = 'Street'
                f.dataType = DataType.STRING
                f.value = 'streetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlongstreetlong'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'suburb'
                f.name = 'Suburb'
                f.dataType = DataType.SUBURB
                f.value = 'sub22urb'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'postcode'
                f.name = 'postcode'
                f.dataType = DataType.POSTCODE
                f.value = 'postcode'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'country'
                f.name = 'country'
                f.dataType = DataType.COUNTRY
                f.value = 'Australia'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'homePhoneNumber'
                f.name = 'homePhoneNumber'
                f.dataType = DataType.PHONE
                f.value = '2255515'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'businessPhoneNumber'
                f.name = 'businessPhoneNumber'
                f.dataType = DataType.PHONE
                f.value = '2255515'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'faxNumber'
                f.name = 'faxNumber'
                f.dataType = DataType.PHONE
                f.value = '2255515'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'mobilePhoneNumber'
                f.name = 'mobilePhoneNumber'
                f.dataType = DataType.PHONE
                f.value = '2255515'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'dateOfBirth'
                f.name = 'dateOfBirth'
                f.dataType = DataType.DATE
                f.value = '20/07/1891'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'abn'
                f.name = 'abn'
                f.dataType = DataType.STRING
                f.value = 'abn'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMale'
                f.name = 'isMale'
                f.dataType = DataType.ENUM
                f.enumType = 'Gender'
                f.value = '0'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaEmailAllowed'
                f.name = 'isMarketingViaEmailAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'isMarketingViaEmailAllowed'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaPostAllowed'
                f.name = 'isMarketingViaPostAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'isMarketingViaPostAllowed'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaSMSAllowed'
                f.name = 'isMarketingViaSMSAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'isMarketingViaSMSAllowed'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'citizenship'
                f.name = 'citizenship'
                f.dataType = DataType.ENUM
                f.value = 'citizenship'
                f.enumType = 'StudentCitizenship'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'countryOfBirth'
                f.name = 'countryOfBirth'
                f.dataType = DataType.COUNTRY
                f.value = 'countryOfBirth'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'languageHome'
                f.name = 'languageHome'
                f.dataType = DataType.LANGUAGE
                f.value = 'languageHome'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'yearSchoolCompleted'
                f.name = 'yearSchoolCompleted'
                f.dataType = DataType.INTEGER
                f.value = 'yearSchoolCompleted'
                f.mandatory = true
                f
            }

            fields << new Field().with { f ->
                f.key = 'englishProficiency'
                f.name = 'englishProficiency'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentEnglishProficiency'
                f.value = 'englishProficiency'
                f.mandatory = true
                f
            }

            fields << new Field().with { f ->
                f.key = 'indigenousStatus'
                f.name = 'indigenousStatus'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentIndigenousStatus'
                f.value = 'indigenousStatus'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'highestSchoolLevel'
                f.name = 'highestSchoolLevel'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentSchoolLevel'
                f.value = 'highestSchoolLevel'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isStillAtSchool'
                f.name = 'isStillAtSchool'
                f.dataType = DataType.BOOLEAN
                f.value = 'isStillAtSchool'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'priorEducationCode'
                f.name = 'priorEducationCode'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentPriorEducation'
                f.value = 'priorEducationCode'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'labourForceStatus'
                f.name = 'labourForceStatus'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentLabourStatus'
                f.value = 'labourForceStatus'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'disabilityType'
                f.name = 'disabilityType'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentDisabilityType'
                f.value = 'disabilityType'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'specialNeeds'
                f.name = 'specialNeeds'
                f.dataType = DataType.STRING
                f.value = 'disabilityType'
                f.mandatory = true
                f.mandatory = true
                f
            }

            fields << new Field().with { f ->
                f.key = 'customField.contact.carMaker'
                f.name = 'carMaker'
                f.dataType = DataType.STRING
                f.value = 'BMW'
                f.mandatory = true
                f.mandatory = true
                f
            }
            
            it.headings << new ish.oncourse.willow.model.field.FieldHeading(name: "Wrong", description: "description", ordering: 1, fields: fields)

            it
        }
    }

    private ContactFields propperRequest() {
        new ContactFields().with {
            it.contactId = '1001'
            List<Field> fields = []
            fields << new Field().with { f ->
                f.key = 'specialNeeds'
                f.name = 'specialNeeds'
                f.dataType = DataType.STRING
                f.value = 'special needs'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'street'
                f.name = 'Street'
                f.dataType = DataType.STRING
                f.value = 'street'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'suburb'
                f.name = 'Suburb'
                f.dataType = DataType.SUBURB
                f.value = 'Parramata'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'postcode'
                f.name = 'postcode'
                f.dataType = DataType.POSTCODE
                f.value = '6797'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'country'
                f.name = 'country'
                f.dataType = DataType.COUNTRY
                f.value = 'Australia'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'businessPhoneNumber'
                f.name = 'businessPhoneNumber'
                f.dataType = DataType.PHONE
                f.value = '02 5551 5678'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'faxNumber'
                f.name = 'faxNumber'
                f.dataType = DataType.PHONE
                f.value = '02 5551 5678'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'mobilePhoneNumber'
                f.name = 'mobilePhoneNumber'
                f.dataType = DataType.PHONE
                f.value = '0491 570 156'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'dateOfBirth'
                f.name = 'dateOfBirth'
                f.dataType = DataType.DATE
                f.value = '20/07/1991'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMale'
                f.name = 'isMale'
                f.dataType = DataType.ENUM
                f.enumType = 'Gender'
                f.value = '1'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaEmailAllowed'
                f.name = 'isMarketingViaEmailAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'false'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaPostAllowed'
                f.name = 'isMarketingViaPostAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'true'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isMarketingViaSMSAllowed'
                f.name = 'isMarketingViaSMSAllowed'
                f.dataType = DataType.BOOLEAN
                f.value = 'true'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'citizenship'
                f.name = 'citizenship'
                f.dataType = DataType.ENUM
                f.value = '2'
                f.enumType = 'StudentCitizenship'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'countryOfBirth'
                f.name = 'countryOfBirth'
                f.dataType = DataType.COUNTRY
                f.value = 'Australia'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'languageHome'
                f.name = 'languageHome'
                f.dataType = DataType.LANGUAGE
                f.value = 'English'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'yearSchoolCompleted'
                f.name = 'yearSchoolCompleted'
                f.dataType = DataType.INTEGER
                f.value = '2009'
                f.mandatory = true
                f
            }

            fields << new Field().with { f ->
                f.key = 'englishProficiency'
                f.name = 'englishProficiency'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentEnglishProficiency'
                f.value = '1'
                f.mandatory = true
                f
            }

            fields << new Field().with { f ->
                f.key = 'indigenousStatus'
                f.name = 'indigenousStatus'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentIndigenousStatus'
                f.value = '3'
                f.mandatory = true
                f
            }
            
            fields << new Field().with { f ->
                f.key = 'highestSchoolLevel'
                f.name = 'highestSchoolLevel'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentSchoolLevel'
                f.value = '6'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'isStillAtSchool'
                f.name = 'isStillAtSchool'
                f.dataType = DataType.BOOLEAN
                f.value = 'true'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'priorEducationCode'
                f.name = 'priorEducationCode'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentPriorEducation'
                f.value = '1'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'labourForceStatus'
                f.name = 'labourForceStatus'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentLabourStatus'
                f.value = '1'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'disabilityType'
                f.name = 'disabilityType'
                f.dataType = DataType.ENUM
                f.enumType = 'AvetmissStudentDisabilityType'
                f.value = '1'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'customField.contact.carMaker'
                f.name = 'carMaker'
                f.dataType = DataType.STRING
                f.value = 'BMW'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'abn'
                f.name = 'abn'
                f.dataType = DataType.STRING
                f.value = '1234'
                f.mandatory = true
                f
            }
            it.headings << new ish.oncourse.willow.model.field.FieldHeading(name: "Test heading", description: "description", ordering: 1, fields: fields)
            it
        }
    }
}
