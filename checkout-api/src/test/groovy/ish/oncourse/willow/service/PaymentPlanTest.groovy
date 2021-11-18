package ish.oncourse.willow.service

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.WebSite
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.checkout.payment.v2.CreatePaymentModel
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.*

@CompileStatic
class PaymentPlanTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/PaymentPlanTest.xml'
    }
    
    @Test
    void testAmount() {
        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        Amount amount = new ProcessCheckoutModel(context, college, getPPlanModelRequest(['1001', '1002']), financialService).process().model.amount

        assertEquals(amount.total, 430.00d, 0)
        assertEquals(amount.discount, 33.00d, 0)
        assertEquals(amount.payNow, 318.00d, 0)
        assertEquals(amount.minPayNow, 318.00d, 0)
        assertEquals(amount.ccPayment, 69.00d, 0)
        assertTrue(amount.isEditable)
        assertEquals(amount.owing, 79.00d, 0)
        assertEquals(amount.voucherPayments.size(), 2)
        assertEquals(amount.voucherPayments[0].redeemVoucherId, '1001')
        assertEquals(amount.voucherPayments[0].amount, 99.00d, 0)
        assertEquals(amount.voucherPayments[1].redeemVoucherId, '1002')
        assertEquals(amount.voucherPayments[1].amount, 150.00d, 0)
        
        amount = new ProcessCheckoutModel(context, college, getPPlanModelRequest(), financialService).process().model.amount

        assertEquals(amount.total, 430.00d, 0)
        assertEquals(amount.discount, 33.00d, 0)
        assertEquals(amount.payNow, 239.00d, 0)
        assertEquals(amount.minPayNow, 239.00d, 0)
        assertTrue(amount.isEditable)
        assertEquals(amount.owing, 158.00d, 0)

        amount = new ProcessCheckoutModel(context, college, getRegularModelRequest(), financialService).process().model.amount

        assertEquals(amount.total, 210.00d, 0)
        assertEquals(amount.discount, 11.00d, 0)
        assertEquals(amount.payNow, 199.00d, 0)
        assertEquals(amount.minPayNow, 199.00d, 0)
        assertFalse(amount.isEditable)
        assertEquals(amount.owing, 0.00d, 0)
    }


    @Test
    void testPayFully() {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest(), financialService).process()
        
        Contact payer = SelectById.query(Contact, 1001L).selectOne(context)
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(context, college, webSite,
                new PaymentRequest(ccAmount: 397.00d, checkoutModelRequest:getPPlanModelRequest()), processModel.model,financialService, payer)
                .create()
        
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

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest(), financialService).process()
        Contact payer = SelectById.query(Contact, 1001L).selectOne(context)
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(cayenneService.newContext(), college, webSite,
                new PaymentRequest(ccAmount: 300.00d, checkoutModelRequest:getPPlanModelRequest()), processModel.model, financialService, payer)
                .create()

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

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getPPlanModelRequest(), financialService).process()
        try {
            Contact payer = SelectById.query(Contact, 1001L).selectOne(context)

            new CreatePaymentModel(cayenneService.newContext(), college, webSite,
                    new PaymentRequest(ccAmount: 400.00d, checkoutModelRequest:getPPlanModelRequest()), processModel.model,financialService, payer)
                    .create()
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
                n.articles << new Article(contactId: '1001', productId: '1003', selected: true, quantity: 1, price: 100.00d, total: 100.00d)
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
                n.articles << new Article(contactId: '1001', productId: '1003', selected: true, quantity: 1, price: 100.00d, total: 100.00d)
                n
            }
            r.redeemedVoucherIds.addAll(voucherIsd)
            r.payerId = '1001'
            r
        }
    }
    
    
    
    
    
}
