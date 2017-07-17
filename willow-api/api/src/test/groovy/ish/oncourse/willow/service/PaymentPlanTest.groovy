package ish.oncourse.willow.service

import ish.math.Money
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.College
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.WebSite
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.checkout.payment.CreatePaymentModel
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.field.FieldSet
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.*

class PaymentPlanTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/PaymentPlanTest.xml'
    }


    @Test
    void test123() {
        println "${FieldSet.ENROLMENT}.${ContextType.CONTACT}.${FieldProperty.ABN}.required".toLowerCase()
    }
    
    
    
    @Test
    void testAmount() {
        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        Amount amount = new ProcessCheckoutModel(context, college, getPPlanModelRequest(['1001', '1002'])).process().model.amount

        assertEquals(amount.total, 430.00, 0)
        assertEquals(amount.discount, 33.00, 0)
        assertEquals(amount.payNow, 69.00, 0)
        assertEquals(amount.minPayNow, 69.00, 0)
        assertTrue(amount.isEditable)
        assertEquals(amount.owing, 79.00, 0)
        assertEquals(amount.voucherPayments.size(), 2)
        assertEquals(amount.voucherPayments[0].redeemVoucherId, '1001')
        assertEquals(amount.voucherPayments[0].amount, 99.00, 0)
        assertEquals(amount.voucherPayments[1].redeemVoucherId, '1002')
        assertEquals(amount.voucherPayments[1].amount, 150.00, 0)
        
        amount = new ProcessCheckoutModel(context, college, getPPlanModelRequest()).process().model.amount

        assertEquals(amount.total, 430.00, 0)
        assertEquals(amount.discount, 33.00, 0)
        assertEquals(amount.payNow, 239.00, 0)
        assertEquals(amount.minPayNow, 239.00, 0)
        assertTrue(amount.isEditable)
        assertEquals(amount.owing, 158.00, 0)

        amount = new ProcessCheckoutModel(context, college, getRegularModelRequest()).process().model.amount

        assertEquals(amount.total, 210.00, 0)
        assertEquals(amount.discount, 11.00, 0)
        assertEquals(amount.payNow, 199.00, 0)
        assertEquals(amount.minPayNow, 199.00, 0)
        assertFalse(amount.isEditable)
        assertEquals(amount.owing, 0.00, 0)
    }


    @Test
    void testPayFully() {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest()).process()
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(cayenneService.newContext(), college, webSite, new PaymentRequest(payNow: 397.00, checkoutModelRequest:getPPlanModelRequest()), processModel.model).create()
        
        assertNotNull(createPaymentModel.model)
        assertNotNull(createPaymentModel.model.paymentIn)
        assertEquals(3, createPaymentModel.model.invoices.size())

        assertEquals(new Money('397.00'), createPaymentModel.model.paymentIn.amount)
        assertEquals(3, createPaymentModel.model.paymentIn.paymentInLines.size())

        PaymentInLine toMainInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice}
        assertEquals(new Money('199.00'), toMainInvoice.amount)

        PaymentInLine toFirstPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001}
        assertEquals(new Money('99.00'), toFirstPPInvoice.amount)

        PaymentInLine toSecondPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002}
        assertEquals(new Money('99.00'), toSecondPPInvoice.amount)

        context.commitChanges()
    }

    @Test
    void testPayPartially() {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest()).process()
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(cayenneService.newContext(), college, webSite, new PaymentRequest(payNow: 300.00, checkoutModelRequest:getPPlanModelRequest()), processModel.model).create()

        assertNotNull(createPaymentModel.model)
        assertNotNull(createPaymentModel.model.paymentIn)
        assertEquals(3, createPaymentModel.model.invoices.size())

        assertEquals(new Money('300.00'), createPaymentModel.model.paymentIn.amount)
        assertEquals(3, createPaymentModel.model.paymentIn.paymentInLines.size())

        PaymentInLine toMainInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice}
        assertEquals(new Money('199.00'), toMainInvoice.amount)

        PaymentInLine toFirstPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001}
        assertEquals(new Money('81.00'), toFirstPPInvoice.amount)

        PaymentInLine toSecondPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002}
        assertEquals(new Money('20.00'), toSecondPPInvoice.amount)

        context.commitChanges()
    }

    @Test
    void testOverPay() {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest()).process()
        try {
            new CreatePaymentModel(cayenneService.newContext(), college, webSite, new PaymentRequest(payNow: 400.00, checkoutModelRequest:getPPlanModelRequest()), processModel.model).create()
            assertTrue('Exception expected',false)
        } catch (IllegalStateException ignore) {}
     
    }

    private CheckoutModelRequest getPPlanModelRequest(List<String> voucherIsd = Collections.EMPTY_LIST) {
        new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments = [
                        new Enrolment(contactId: '1001', classId: '1001', selected: true),
                        new Enrolment(contactId: '1001', classId: '1002', selected: true),
                        new Enrolment(contactId: '1001', classId: '1003', selected: true)
                ]
                n.articles << new Article(contactId: '1001', productId: '1003', selected: true)
                n
            }
            r.redeemedVoucherIds.addAll(voucherIsd)
            r.payerId = '1001'
            r
        }
    }

    private CheckoutModelRequest getRegularModelRequest(List<String> voucherIsd = Collections.EMPTY_LIST) {
        new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments <<  new Enrolment(contactId: '1001', classId: '1003', selected: true)
                n.articles << new Article(contactId: '1001', productId: '1003', selected: true)
                n
            }
            r.redeemedVoucherIds.addAll(voucherIsd)
            r.payerId = '1001'
            r
        }
    }
    
    
    
    
    
}
