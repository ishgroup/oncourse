package ish.oncourse.willow.service

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.PaymentIn
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
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.*

class RedeemVoucherTest extends ApiTest {

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/RedeemVoucherTest.xml'
    }
    
    private CheckoutModelRequest getModelRequest(boolean addProduct = true) {
        new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments = [
                        new Enrolment(contactId: '1001', classId: '1001', selected: true),
                        new Enrolment(contactId: '1001', classId: '1002', selected: true),
                        new Enrolment(contactId: '1001', classId: '1003', selected: true)
                ]
                if (addProduct) {
                    n.articles = [new Article(contactId: '1001', productId: '1003', selected: true,quantity: 1, price: 100.00, total: 100.00, fieldHeadings: [])]
                }
                n
            }
            r.redeemedVoucherIds = ['1001', '1002']
            r.payerId = '1001'
            r

        }
    }

    private PaymentRequest getPaymentRequest(boolean addProduct = true) {
        new PaymentRequest().with { r ->
            r.ccAmount = addProduct ? 69.00 : 0.00
            r.checkoutModelRequest = getModelRequest(addProduct)
            r
        }
    }
    
    @Test
    void testAmount () {
        
        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        CheckoutModelRequest checkoutModelRequest = getModelRequest()
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, checkoutModelRequest, financialService).process()
        Amount amount = processModel.model.amount
        assertEquals(amount.total, 430.00, 0)
        assertEquals(amount.discount, 33.00, 0)
        assertEquals(amount.payNow, 318.00, 0)
        assertEquals(amount.ccPayment, 69.00, 0)
        assertEquals(amount.owing, 79.00, 0)
        assertEquals(amount.voucherPayments.size(), 2)
        assertEquals(amount.voucherPayments[0].redeemVoucherId, '1001')
        assertEquals(amount.voucherPayments[0].amount, 99.00, 0)
        assertEquals(amount.voucherPayments[1].redeemVoucherId, '1002')
        assertEquals(amount.voucherPayments[1].amount, 150.00, 0)
    }
    
    @Test
    void testOnlyVouchersAmount () {
        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        CheckoutModelRequest checkoutModelRequest = getModelRequest(false)
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, checkoutModelRequest, financialService).process()
        Amount amount = processModel.model.amount
        assertEquals(amount.total, 330.00, 0)
        assertEquals(amount.discount, 33.00, 0)
        assertEquals(amount.payNow, 218.0, 0)
        assertEquals(amount.ccPayment, 0.0, 0)
        assertEquals(amount.owing, 79.00, 0)
        assertEquals(amount.voucherPayments.size(), 2)
        assertEquals(amount.voucherPayments[0].redeemVoucherId, '1001')
        assertEquals(amount.voucherPayments[0].amount, 99.00, 0)
        assertEquals(amount.voucherPayments[1].redeemVoucherId, '1002')
        assertEquals(amount.voucherPayments[1].amount, 119.00, 0)
    }
    
    @Test
    void testPaymentModel () {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college
        
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getModelRequest(),financialService).process()

        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(cayenneService.newContext(), college, webSite, paymentRequest, processModel.model,financialService).create()

        assertNotNull(createPaymentModel.model)

        assertNotNull(createPaymentModel.model.paymentIn)
        assertEquals(3, createPaymentModel.model.invoices.size())

        assertEquals(new Money('69.00'), createPaymentModel.model.paymentIn.amount)
        assertEquals(3, createPaymentModel.model.paymentIn.paymentInLines.size())

        PaymentInLine toMainInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice}
        assertEquals(new Money('49.00'), toMainInvoice.amount)
        
        PaymentInLine toPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002}
        assertEquals(new Money('20.00'), toPPInvoice.amount)
        
        assertEquals(2, createPaymentModel.model.voucherPayments.size())
        
        PaymentIn courseVoucherPayment = createPaymentModel.model.voucherPayments.find { it.voucher.id == 1001}
        assertEquals(new Money('99.00'), courseVoucherPayment.amount)
        assertEquals(1, courseVoucherPayment.paymentInLines.size())
        assertEquals(new Money('99.00'), courseVoucherPayment.paymentInLines[0].amount)
        assertEquals(new Money('99.00'), courseVoucherPayment.paymentInLines[0].amount)
        assertEquals(1, courseVoucherPayment.voucherPaymentIns.size())
        assertNotNull(courseVoucherPayment.voucherPaymentIns[0].invoiceLine)
        assertEquals(new Money('99.00'),courseVoucherPayment.voucherPaymentIns[0].invoiceLine.finalPriceToPayIncTax)
        assertNotNull(courseVoucherPayment.voucherPaymentIns[0].invoiceLine.enrolment)
        assertEquals(1001, courseVoucherPayment.voucherPaymentIns[0].invoiceLine.enrolment.courseClass.id)

        PaymentIn moneyVoucherPayment = createPaymentModel.model.voucherPayments.find { it.voucher.id == 1002 }
        
        assertEquals(new Money('150.00'), moneyVoucherPayment.amount)
        assertEquals(1, moneyVoucherPayment.paymentInLines.size())

        PaymentInLine linetoMainInvoice =  moneyVoucherPayment.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice }
        assertNotNull(linetoMainInvoice)
        assertEquals(new Money('150.00'), linetoMainInvoice.amount)

        context.commitChanges()
    }


    @Test
    void testOnlyVoucherPayments () {

        ObjectContext context = cayenneService.newContext()
        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        College college = webSite.college

        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, getModelRequest(false), financialService).process()

        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(cayenneService.newContext(), college, webSite, getPaymentRequest(false), processModel.model,financialService).create()

        assertNotNull(createPaymentModel.model)

        assertNotNull(createPaymentModel.model.paymentIn)
        assertEquals(3, createPaymentModel.model.invoices.size())

        assertEquals(new Money('0.00'), createPaymentModel.model.paymentIn.amount)
        assertEquals(3, createPaymentModel.model.paymentIn.paymentInLines.size())

        PaymentInLine toMainInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice}
        assertEquals(new Money('0.00'), toMainInvoice.amount)

        PaymentInLine toCoutsePPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001}
        assertEquals(new Money('00.00'), toCoutsePPInvoice.amount)

        PaymentInLine toMoneyPPInvoice = createPaymentModel.model.paymentIn.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002}
        assertEquals(new Money('00.00'), toMoneyPPInvoice.amount)
        assertEquals(2, createPaymentModel.model.voucherPayments.size())

        PaymentIn courseVoucherPayment = createPaymentModel.model.voucherPayments.find { it.voucher.id == 1001}
        assertEquals(new Money('99.00'), courseVoucherPayment.amount)
        assertEquals(1, courseVoucherPayment.paymentInLines.size())
        assertEquals(new Money('99.00'), courseVoucherPayment.paymentInLines[0].amount)
        assertEquals(new Money('99.00'), courseVoucherPayment.paymentInLines[0].amount)
        assertEquals(1, courseVoucherPayment.voucherPaymentIns.size())
        assertNotNull(courseVoucherPayment.voucherPaymentIns[0].invoiceLine)
        assertEquals(new Money('99.00'),courseVoucherPayment.voucherPaymentIns[0].invoiceLine.finalPriceToPayIncTax)
        assertNotNull(courseVoucherPayment.voucherPaymentIns[0].invoiceLine.enrolment)
        assertEquals(1001, courseVoucherPayment.voucherPaymentIns[0].invoiceLine.enrolment.courseClass.id)

        PaymentIn moneyVoucherPayment = createPaymentModel.model.voucherPayments.find { it.voucher.id == 1002 }

        assertEquals(new Money('119.00'), moneyVoucherPayment.amount)
        assertEquals(2, moneyVoucherPayment.paymentInLines.size())

        toMainInvoice =  moneyVoucherPayment.paymentInLines.find {it.invoice == createPaymentModel.mainInvoice }
        assertNotNull(toMainInvoice)
        assertEquals(new Money('99.00'), toMainInvoice.amount)

        toCoutsePPInvoice =  moneyVoucherPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001 }
        assertNull(toCoutsePPInvoice)

        toMoneyPPInvoice =  moneyVoucherPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002 }
        assertNotNull(toMoneyPPInvoice)
        assertEquals(new Money('20.00'), toMoneyPPInvoice.amount)

        context.commitChanges()
    }
    
    
    
    
}
