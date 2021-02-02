package ish.oncourse.willow.service

import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.checkout.payment.CreatePaymentModel
import ish.oncourse.willow.checkout.payment.ProcessPaymentModel
import ish.oncourse.willow.checkout.payment.ValidateCreditCardForm
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull

class MakePaymentTest extends AbstractPaymentTest {

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/MakePaymentTest.xml'
    }

    @Test
    void makePaymentTest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CollegeService service = new CollegeService(cayenneService)
        WebSite webSite = service.webSite
        College college = webSite.college

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        
        PaymentRequest request = buildPaymentRequest()
        CheckoutModel model = api.getCheckoutModel(request.checkoutModelRequest)
        assertNull(model.error)
        assertEquals(request.ccAmount, model.amount.payNow, 0)
        assertEquals(1, model.contactNodes.size())
        assertEquals(1, model.contactNodes[0].enrolments.size())
        assertTrue(model.contactNodes[0].enrolments[0].selected)
        assertTrue(model.contactNodes[0].enrolments[0].errors.empty)
        assertEquals(1, model.contactNodes[0].applications.size())
        assertTrue(model.contactNodes[0].applications[0].selected)
        assertTrue(model.contactNodes[0].applications[0].errors.empty)
        assertEquals(1, model.contactNodes[0].vouchers.size())
        assertTrue(model.contactNodes[0].vouchers[0].selected)
        assertTrue(model.contactNodes[0].vouchers[0].errors.empty)

        ObjectContext ctx = cayenneService.newContext()
        ValidationError validationError = new ValidateCreditCardForm(request, ctx).validate(false)
        assertTrue(validationError.formErrors.empty)
        assertTrue(validationError.fieldsErrors.empty)

        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(ctx, college, webSite, request, model, financialService).create()
        PaymentInModel paymentInModel = createPaymentModel.model

        assertEquals(1, createPaymentModel.applications.size())
        assertEquals(1001l, createPaymentModel.applications[0].student.id)
        assertEquals(1002l, createPaymentModel.applications[0].course.id)
        assertEquals(ApplicationStatus.NEW, createPaymentModel.applications[0].status)

        assertEquals(new Money('166.00'), paymentInModel.paymentIn.amount)
        assertEquals(ish.common.types.PaymentStatus.IN_TRANSACTION, paymentInModel.paymentIn.status)

        assertEquals(request.sessionId, paymentInModel.paymentIn.sessionId)
        assertEquals(1, paymentInModel.paymentIn.paymentInLines.size())
        assertEquals(new Money('166.00'), paymentInModel.paymentIn.paymentInLines[0].amount)
        assertEquals(1, paymentInModel.invoices.size())
        assertEquals(new Money('166.00'), paymentInModel.invoices[0].amountOwing)
        assertEquals(request.sessionId, paymentInModel.invoices[0].sessionId)
        assertEquals(2, paymentInModel.invoices[0].invoiceLines.size())
        
        assertNotNull(paymentInModel.invoices[0].invoiceLines[0].enrolment)
        assertEquals(EnrolmentStatus.IN_TRANSACTION, paymentInModel.invoices[0].invoiceLines[0].enrolment.status)
        
        ish.oncourse.model.Enrolment enrolment = paymentInModel.invoices[0].invoiceLines[0].enrolment
        assertEquals(1001l, enrolment.student.id)
        assertEquals(1001l, enrolment.courseClass.id)
        assertEquals(1, paymentInModel.invoices[0].invoiceLines[0].invoiceLineDiscounts.size())
        assertEquals(1003l, paymentInModel.invoices[0].invoiceLines[0].invoiceLineDiscounts[0].discount.id)
        assertEquals(new Money('66.00'), paymentInModel.invoices[0].invoiceLines[0].finalPriceToPayIncTax)
        assertEquals(new Money('44.00'), paymentInModel.invoices[0].invoiceLines[0].discountTotalIncTax)
        assertEquals(new Money('110.00'), paymentInModel.invoices[0].invoiceLines[0].priceTotalIncTax)
        assertEquals(0, paymentInModel.invoices[0].invoiceLines[0].sortOrder)

        assertEquals(1, paymentInModel.invoices[0].invoiceLines[1].productItems.size())
        assertTrue(paymentInModel.invoices[0].invoiceLines[1].productItems[0] instanceof ish.oncourse.model.Voucher)
        assertEquals(new Money('100.00'), paymentInModel.invoices[0].invoiceLines[1].priceTotalIncTax)
        assertEquals(1, paymentInModel.invoices[0].invoiceLines[1].sortOrder)

        ish.oncourse.model.Voucher voucher = paymentInModel.invoices[0].invoiceLines[1].productItems[0] as ish.oncourse.model.Voucher
        assertEquals(ProductStatus.NEW, voucher.status)
        assertEquals(7l, voucher.product.id)
        assertEquals(new Money('100.00'), voucher.valueRemaining)
        assertEquals(new Money('100.00'), voucher.valueOnPurchase)
        assertEquals(1003l, paymentInModel.invoices[0].invoiceLines[0].invoiceLineDiscounts[0].discount.id)

        ProcessPaymentModel processPaymentModel = new ProcessPaymentModel(ctx, cayenneService.newNonReplicatingContext(), college, createPaymentModel, request).process()
        assertEquals(PaymentStatus.SUCCESSFUL,  processPaymentModel.response.status)

        assertEquals(ish.common.types.PaymentStatus.SUCCESS, paymentInModel.paymentIn.status)
        assertEquals(ConfirmationStatus.NOT_SENT, paymentInModel.paymentIn.confirmationStatus)

        assertEquals(ConfirmationStatus.NOT_SENT, paymentInModel.invoices[0].confirmationStatus)


        assertEquals(EnrolmentStatus.SUCCESS, enrolment.status)
        assertEquals(ConfirmationStatus.NOT_SENT, enrolment.confirmationStatus)

        assertEquals(ProductStatus.ACTIVE, voucher.status)
        assertEquals(ConfirmationStatus.NOT_SENT, voucher.confirmationStatus)
    }

    @Test
    void makeFailedPayment() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CollegeService service = new CollegeService(cayenneService)
        WebSite webSite = service.webSite
        College college = webSite.college

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)

        PaymentRequest request = buildPaymentRequest()
        CheckoutModel model = api.getCheckoutModel(request.checkoutModelRequest)

        ObjectContext ctx = cayenneService.newContext()
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(ctx, college, webSite, request, model, financialService).create()
        PaymentInModel paymentInModel = createPaymentModel.model
        ish.oncourse.model.Enrolment enrolment = paymentInModel.invoices[0].invoiceLines[0].enrolment
        ish.oncourse.model.Voucher voucher = paymentInModel.invoices[0].invoiceLines[1].productItems[0] as ish.oncourse.model.Voucher

        request.creditCardNumber = '5105105105105100'
        ProcessPaymentModel processPaymentModel = new ProcessPaymentModel(ctx, cayenneService.newNonReplicatingContext(), college, createPaymentModel, request).process()
        assertEquals(PaymentStatus.FAILED,  processPaymentModel.response.status)

        assertEquals(ish.common.types.PaymentStatus.FAILED_CARD_DECLINED, paymentInModel.paymentIn.status)
        assertEquals(ConfirmationStatus.DO_NOT_SEND, paymentInModel.paymentIn.confirmationStatus)

        assertEquals(ConfirmationStatus.DO_NOT_SEND, paymentInModel.invoices[0].confirmationStatus)
        
        assertEquals(EnrolmentStatus.FAILED, enrolment.status)
        assertEquals(ConfirmationStatus.DO_NOT_SEND, enrolment.confirmationStatus)

        assertEquals(ProductStatus.CANCELLED, voucher.status)
        assertEquals(ConfirmationStatus.DO_NOT_SEND, voucher.confirmationStatus)
    }
}
