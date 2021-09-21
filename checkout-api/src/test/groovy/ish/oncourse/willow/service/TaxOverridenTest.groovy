package ish.oncourse.willow.service

import groovy.transform.CompileStatic
import ish.common.types.EnrolmentStatus
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Voucher
import ish.oncourse.model.WebSite
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.checkout.payment.v2.CreatePaymentModel
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

@CompileStatic
class TaxOverridenTest extends AbstractPaymentTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/TaxOverridenTest.xml'
    }

    @Test
    void testTaxOverriden() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CollegeService service = new CollegeService(cayenneService)
        WebSite webSite = service.webSite
        College college = webSite.college

        ObjectContext context = cayenneService.newContext()

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)

        PaymentRequest request = buildPaymentRequest()
        request.ccAmount = 169D
        CheckoutModel model = api.getCheckoutModel(request.checkoutModelRequest)

        assertEquals(169D, model.amount.payNow, 0)
        assertEquals(215D, model.amount.total, 0)
        assertEquals(46D, model.amount.discount, 0)

        assertEquals(115D, model.contactNodes[0].enrolments[0].price.fee, 0)
        assertEquals(69D, model.contactNodes[0].enrolments[0].price.appliedDiscount.discountedFee, 0)
        assertEquals(46D, model.contactNodes[0].enrolments[0].price.appliedDiscount.discountValue, 0)

        model.amount
        Contact payer = SelectById.query(Contact, 1001L).selectOne(context)

        CreatePaymentModel createPaymentModel = new CreatePaymentModel(context, college, webSite, request, model,financialService, payer).create()
        PaymentInModel paymentInModel = createPaymentModel.model

        assertEquals(new Money('169.00'), paymentInModel.paymentIn.amount)

        assertEquals(1, paymentInModel.paymentIn.paymentInLines.size())
        assertEquals(new Money('169.00'), paymentInModel.paymentIn.paymentInLines[0].amount)
        assertEquals(1, paymentInModel.invoices.size())
        assertEquals(new Money('169.00'), paymentInModel.invoices[0].amountOwing)


        Enrolment enrolment = paymentInModel.invoices[0].invoiceLines[0].enrolment
        assertEquals(1, paymentInModel.invoices[0].invoiceLines[0].invoiceLineDiscounts.size())
        assertEquals(1003l, paymentInModel.invoices[0].invoiceLines[0].invoiceLineDiscounts[0].discount.id)
        assertEquals(new Money('69.00'), paymentInModel.invoices[0].invoiceLines[0].finalPriceToPayIncTax)
        assertEquals(new Money('46.00'), paymentInModel.invoices[0].invoiceLines[0].discountTotalIncTax)
        assertEquals(new Money('115.00'), paymentInModel.invoices[0].invoiceLines[0].priceTotalIncTax)

        assertEquals(1, paymentInModel.invoices[0].invoiceLines[1].productItems.size())
        assertTrue(paymentInModel.invoices[0].invoiceLines[1].productItems[0] instanceof Voucher)
        assertEquals(new Money('100.00'), paymentInModel.invoices[0].invoiceLines[1].priceTotalIncTax)
        assertEquals(1, paymentInModel.invoices[0].invoiceLines[1].sortOrder)

        Voucher voucher = paymentInModel.invoices[0].invoiceLines[1].productItems[0] as Voucher
        assertEquals(ProductStatus.NEW, voucher.status)
        assertEquals(7l, voucher.product.id)
        assertEquals(new Money('100.00'), voucher.valueRemaining)
        assertEquals(new Money('100.00'), voucher.valueOnPurchase)
    }
}
