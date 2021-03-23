package ish.oncourse.services.paymentexpress

import ish.common.types.PaymentStatus
import ish.common.types.ProductStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.services.ServiceTestModule
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.test.LoadDataSet
import ServiceTest
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class NewPaymentExpressGatewayServiceIntegrationTest extends ServiceTest {

	ICayenneService cayenneService

	@Before
	void before() {
		initTest("ish.oncourse.services.paymentexpress", "service", ServiceTestModule.class)
		new LoadDataSet().dataSetFile("ish/oncourse/services/paymentexpress/newPaymentExpressDataSet.xml").load(testContext.getDS())
		cayenneService = getService(ICayenneService)
	}

	@Test
	void testSuccess() {
		def context = cayenneService.newContext()
		def payment = SelectById.query(PaymentIn, 1l).selectOne(context)
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().model
		def service = new NewPaymentExpressGatewayService(cayenneService.newNonReplicatingContext())
		service.submit(model)

		assertEquals(payment.status, PaymentStatus.SUCCESS)
		model.invoices[0].invoiceLines[0].productItems.each { p ->
			assertEquals(p.status, ProductStatus.ACTIVE)
		}

		def transactions = ObjectSelect.query(PaymentTransaction).select(context)
		assertEquals(1, transactions.size())
		assertTrue(transactions[0].isFinalised)
	}

	@Test
	void testFail() {
		def context = cayenneService.newContext()
		def payment = SelectById.query(PaymentIn, 2l).selectOne(context)
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().model
		def service = new NewPaymentExpressGatewayService(cayenneService.newNonReplicatingContext())
		service.submit(model)

		assertEquals(payment.status, PaymentStatus.FAILED_CARD_DECLINED)
		model.invoices[0].invoiceLines[0].productItems.each { p ->
			assertEquals(p.status, ProductStatus.NEW)
		}

		def transactions = ObjectSelect.query(PaymentTransaction).select(context)
		assertEquals(1, transactions.size())
		assertTrue(transactions[0].isFinalised)
	}
}
