package ish.oncourse.services.paymentexpress

import ish.common.types.PaymentStatus
import ish.common.types.ProductStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.services.ServiceModule
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.test.DataSetInitializer
import ish.oncourse.test.ServiceTest
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals


class NewPaymentExpressGatewayServiceIntegrationTest extends ServiceTest {

	def ICayenneService cayenneService
	
	@Before
	public void before() {
		initTest("ish.oncourse.services.paymentexpress", "service", ServiceModule.class);

		DataSetInitializer.initDataSets("ish/oncourse/services/paymentexpress/newPaymentExpressDataSet.xml");
		cayenneService = getService(ICayenneService)
	}
	
	@Test
	def void testSuccess() {
		def context = cayenneService.newContext()
		def payment = SelectById.query(PaymentIn, 1l).selectOne(context)
		def PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().model
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
	def void testFail() {
		def context = cayenneService.newContext()
		def payment = SelectById.query(PaymentIn, 2l).selectOne(context)
		def PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().model
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
