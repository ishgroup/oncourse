package ish.oncourse.willow.service

import ish.common.types.EnrolmentStatus
import ish.oncourse.model.Enrolment
import ish.oncourse.model.EnrolmentCustomField
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

class EnrolmentFieldsTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/EnrolmentFieldsTest.xml'
    }


    @Test
    void testGet() {

        ObjectContext context = cayenneService.newContext()

        SelectById.query(ish.oncourse.model.FieldHeading, 1l).selectOne(context).order = 1
        SelectById.query(ish.oncourse.model.FieldHeading, 2l).selectOne(context).order = 2
        context.commitChanges()

        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)

        ContactNode node = api.getContactNode(new ContactNodeRequest().with { cn ->
            cn.contactId = '1002'
            cn.classIds << '1001'
            cn
            })

        List<FieldHeading> headings = node.enrolments[0].fieldHeadings
        assertNotNull(headings)
        assertEquals(3, headings.size())
        assertNull(headings[0].name)
        assertNull(headings[0].description)
        assertEquals(1, headings[0].fields.size())
        assertEquals('customField.enrolment.enrolmentNotes', headings[0].fields[0].key)

        assertEquals('First Heading', headings[1].name)
        assertEquals('description', headings[1].description)
        assertEquals(1, headings[1].fields.size())
        assertEquals('customField.enrolment.enrolmentKey', headings[1].fields[0].key)

        assertEquals('Second Heading', headings[2].name)
        assertEquals('description', headings[2].description)
        assertEquals(1, headings[2].fields.size())
        assertEquals('customField.enrolment.enrolmentFee', headings[2].fields[0].key)

        CheckoutModelRequest modelRequest = new CheckoutModelRequest().with { mr ->
            mr.contactNodes << node
            mr.payerId = '1002'
            mr
        }
        
        CheckoutModel model = api.getCheckoutModel(modelRequest)


        assertNull(model.error)
        assertNotNull(model.validationErrors)
        assertEquals(3, model.validationErrors.formErrors.size())
        assertEquals(3, model.validationErrors.fieldsErrors.size())
        assertEquals(3, model.validationErrors.fieldsErrors.size())
        
        assertEquals('customField.enrolment.enrolmentNotes', model.validationErrors.fieldsErrors[0].name)
        assertEquals('Enrolment Notes for Managerial Accounting (AAV - 1) is required', model.validationErrors.fieldsErrors[0].error)

        assertEquals('customField.enrolment.enrolmentKey', model.validationErrors.fieldsErrors[1].name)
        assertEquals('Enrolment Key for Managerial Accounting (AAV - 1) is required', model.validationErrors.fieldsErrors[1].error)

        assertEquals('customField.enrolment.enrolmentFee', model.validationErrors.fieldsErrors[2].name)
        assertEquals('Enrolment Fee for Managerial Accounting (AAV - 1) is required', model.validationErrors.fieldsErrors[2].error)

        headings[0].fields[0].value = 'notes value'
        headings[1].fields[0].value = 'key value'
        headings[2].fields[0].value = 'fee value'
        
        api.makePayment(new PaymentRequest().with { pr ->
            pr.checkoutModelRequest = modelRequest
            pr.creditCardNumber = '5431111111111111'
            pr.creditCardName = 'john smith'
            pr.expiryMonth = '11'
            pr.expiryYear = '2027'
            pr.creditCardCvv = '321'
            pr.agreementFlag = true
            pr.sessionId = 'paymentRandomSession'
            pr.ccAmount = model.amount.payNow
            pr
        })
        
        Enrolment e = ObjectSelect.query(Enrolment).selectFirst(cayenneService.newContext())
        assertEquals(EnrolmentStatus.SUCCESS, e.status)
        assertEquals(3, e.customFields.size())

        EnrolmentCustomField enrolmentNotes = e.customFields.find { cf -> cf.customFieldType.key == 'enrolmentNotes'}
        assertEquals('notes value', enrolmentNotes.value)

        EnrolmentCustomField enrolmentKey = e.customFields.find { cf -> cf.customFieldType.key == 'enrolmentKey'}
        assertEquals('key value', enrolmentKey.value)
        
        EnrolmentCustomField enrolmentFee = e.customFields.find { cf -> cf.customFieldType.key == 'enrolmentFee'}
        assertEquals('fee value', enrolmentFee.value)
    }
}
