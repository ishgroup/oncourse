package ish.oncourse.services.payment

import ish.common.types.CreditCardType
import ish.common.types.PaymentStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.services.ServiceTestModule
import ish.oncourse.services.cache.NoopQueryCache
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.test.LoadDataSet
import ServiceTest
import org.apache.tapestry5.internal.test.TestableRequest
import org.apache.tapestry5.ioc.Messages
import org.junit.Before
import org.junit.Test

import static ish.oncourse.services.paymentexpress.TestPaymentGatewayService.MASTERCARD
import static junit.framework.TestCase.*
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by Artem on 27/12/2016.
 */
class NewPaymentProcessorTest extends ServiceTest {

    ICayenneService cayenneService
    Messages messages
    String nextYear


    @Before
    void before() {
        initTest("ish.oncourse.services.payment", "service", new NoopQueryCache(), ServiceTestModule)

        new LoadDataSet().dataSetFile("ish/oncourse/services/payment/NewPaymentProcessorTestDataSet.xml")
                .load(getDataSource())

        cayenneService = getService(ICayenneService)
        messages = mock(Messages.class)
        when(messages.get(anyString())).thenReturn("Any string")

        Calendar calendar =  Calendar.getInstance()
        calendar.time = new Date()
        Integer year = calendar.get(Calendar.YEAR)
        nextYear = String.valueOf(++year)
    }


    @Test
    void testUnknownResult() {
        NewPaymentProcessController controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.READY_TO_PROCESS, controller.state)
        controller.proceedToDetails()

        controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.FILL_CC_DETAILS, controller.state)

        PaymentRequest request = new PaymentRequest(name: 'DPS unknown result', number: '5105105105105100', cvv: '321', month: '11', year: nextYear)
        PaymentResponse response = new PaymentResponse()
        controller.processPayment(request, response)

        assertEquals(GetPaymentState.PaymentState.DPS_ERROR, response.status)


        controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(PaymentStatus.IN_TRANSACTION, controller.model.paymentIn.status)
        assertEquals(1, controller.model.paymentIn.paymentTransactions.size())
        assertEquals(false, controller.model.paymentIn.paymentTransactions.get(0).isFinalised)

        assertEquals(GetPaymentState.PaymentState.DPS_ERROR, controller.state)
    }

    @Test
    void testInvalidAction() {
        NewPaymentProcessController controller = getPaymentController('sessionId1')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.DPS_PROCESSING, controller.state)
        PaymentRequest request = new PaymentRequest(name: 'JOHN', number: '5105105105105100', cvv: '321', month: '11', year: nextYear)
        PaymentResponse response = new PaymentResponse()

        controller.processPayment(request, response)

        assertEquals(GetPaymentState.PaymentState.ERROR, response.status)

        controller = getPaymentController('sessionId1')

        assertEquals(GetPaymentState.PaymentState.DPS_PROCESSING, controller.state)
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.DPS_PROCESSING, controller.state)
        assertEquals(1, controller.model.paymentIn.paymentTransactions.size())
        assertEquals(false, controller.model.paymentIn.paymentTransactions.get(0).isFinalised)
    }


    @Test
    void testInvalidCC() {
        NewPaymentProcessController controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.READY_TO_PROCESS, controller.state)
        controller.proceedToDetails()

        controller = getPaymentController('sessionId')

        assertEquals(GetPaymentState.PaymentState.FILL_CC_DETAILS, controller.state)
        assertEquals(PaymentStatus.CARD_DETAILS_REQUIRED, controller.model.paymentIn.status)
        assertTrue(controller.model.paymentIn.paymentTransactions.empty)

        PaymentRequest request = new PaymentRequest(name: '', number: '5105105105105100', cvv: '321', month: '11', year: nextYear)
        PaymentResponse response = new PaymentResponse()

        controller.processPayment(request, response)

        assertEquals(GetPaymentState.PaymentState.WARNING, response.status)
        assertNotNull(response.cardNameError)
        assertNull(response.cardCVVError)
        assertNull(response.cardNumberError)
        assertNull(response.cardExpiryDateError)
    }

    @Test
    void testRetry() {
        NewPaymentProcessController controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.READY_TO_PROCESS, controller.state)
        controller.proceedToDetails()

        controller = getPaymentController('sessionId')

        PaymentRequest request = new PaymentRequest(name: 'JOHN JOHN', number: MASTERCARD.number, cvv: MASTERCARD.cvv, month: MASTERCARD.expiry.split('/')[0], year:  MASTERCARD.expiry.split('/')[1])
        PaymentResponse response = new PaymentResponse()
        controller.processPayment(request, response)
        testPaymentFields(controller.model.paymentIn, request)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, controller.state)
        assertNotNull(controller.model.paymentIn)
        assertEquals(PaymentStatus.IN_TRANSACTION, controller.model.paymentIn.status)
        assertTrue(controller.model.paymentIn.paymentTransactions.empty)
        assertEquals(1, controller.model.failedPayments.size())
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, controller.model.failedPayments.get(0).status)
        assertEquals(1, controller.model.failedPayments.get(0).paymentTransactions.size())
        assertEquals(true, controller.model.failedPayments.get(0).paymentTransactions.get(0).isFinalised)

        response = new PaymentResponse()
        controller.tryOtherCard(response)
        assertEquals(GetPaymentState.PaymentState.FILL_CC_DETAILS, response.status)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.FILL_CC_DETAILS, controller.state)
        request = new PaymentRequest(name: MASTERCARD.name, number: MASTERCARD.number, cvv: MASTERCARD.cvv, month: MASTERCARD.expiry.split('/')[0], year:  MASTERCARD.expiry.split('/')[1])
        response = new PaymentResponse()
        controller.processPayment(request, response)
        assertEquals(GetPaymentState.PaymentState.SUCCESS, response.status)
        testPaymentFields(controller.model.paymentIn, request)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.SUCCESS, controller.state)
        assertNull(controller.model.paymentIn)
        assertNotNull(controller.model.successPayment)
        assertEquals(PaymentStatus.SUCCESS, controller.model.successPayment.status)
        assertEquals(1, controller.model.successPayment.paymentTransactions.size())

        assertTrue(controller.model.successPayment.paymentTransactions.get(0).isFinalised)

    }


    @Test
    void testAbandon() {
        NewPaymentProcessController controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.READY_TO_PROCESS, controller.state)
        controller.proceedToDetails()

        controller = getPaymentController('sessionId')
        PaymentRequest request = new PaymentRequest(name: 'JOHN JOHN', number: '5105105105105100', cvv: '321', month: '11', year: nextYear)
        PaymentResponse response = new PaymentResponse()
        controller.processPayment(request, response)
        testPaymentFields(controller.model.paymentIn, request)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, controller.state)
        assertNotNull(controller.model.paymentIn)
        assertEquals(PaymentStatus.IN_TRANSACTION, controller.model.paymentIn.status)
        assertTrue(controller.model.paymentIn.paymentTransactions.empty)
        assertEquals(1, controller.model.failedPayments.size())
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, controller.model.failedPayments.get(0).status)
        assertEquals(1, controller.model.failedPayments.get(0).paymentTransactions.size())
        assertEquals(true, controller.model.failedPayments.get(0).paymentTransactions.get(0).isFinalised)

        response = new PaymentResponse()
        controller.abandonPaymentKeepInvoice(response)
        assertEquals(GetPaymentState.PaymentState.FAILED, response.status)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.FAILED, controller.state)

        assertNull(controller.model.paymentIn)
        assertNull(controller.model.successPayment)
        assertEquals(2, controller.model.failedPayments.size())

    }


    @Test
    void testCancel() {
        NewPaymentProcessController controller = getPaymentController('sessionId')
        assertNotNull(controller.model.paymentIn)
        assertEquals(GetPaymentState.PaymentState.READY_TO_PROCESS, controller.state)
        controller.proceedToDetails()

        controller = getPaymentController('sessionId')
        PaymentResponse response = new PaymentResponse()
        controller.abandonPaymentKeepInvoice(response)
        assertEquals(GetPaymentState.PaymentState.FAILED, response.status)

        controller = getPaymentController('sessionId')
        assertEquals(GetPaymentState.PaymentState.FAILED, controller.state)
        assertNull(controller.model.paymentIn)
        assertNull(controller.model.successPayment)
        assertEquals(1, controller.model.failedPayments.size())
        assertEquals(PaymentStatus.FAILED, controller.model.failedPayments.get(0).status)
        assertTrue(controller.model.failedPayments.get(0).paymentTransactions.empty)

    }

    private static testPaymentFields(PaymentIn paymentIn, PaymentRequest request) {
        assertEquals(paymentIn.creditCardName, request.name)
        assertEquals(paymentIn.creditCardCVV, 'XXX')
        assertEquals(paymentIn.creditCardExpiry, "$request.month/$request.year")
        assertEquals(paymentIn.creditCardType, CreditCardType.MASTERCARD)
        assertEquals(paymentIn.creditCardNumber, "${request.number.substring(0,6)}XXXXXXX${request.number.substring(13)}")

    }

    private getPaymentController(String sessionId) {
        new PaymentControllerBuilder(sessionId, getService(INewPaymentGatewayServiceBuilder), cayenneService, messages, getService(TestableRequest)).build()

    }
}
