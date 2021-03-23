package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub
import com.paymentexpress.stubs.TransactionDetails
import com.paymentexpress.stubs.TransactionResult2
import ish.common.types.CreditCardType
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.PaymentIn
import ServiceTest
import ish.oncourse.util.payment.PaymentInModel
import org.junit.Before
import org.junit.Test

import java.rmi.RemoteException

import static ish.common.types.PaymentStatus.FAILED_CARD_DECLINED
import static ish.common.types.PaymentStatus.SUCCESS
import static ish.oncourse.services.paymentexpress.IPaymentGatewayService.FAILED_PAYMENT_IN
import static ish.oncourse.services.paymentexpress.IPaymentGatewayService.SUCCESS_PAYMENT_IN
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

class NewPaymentExpressGatewayServiceTest extends ServiceTest {

	def PaymentIn paymentIn
	def PaymentInModel model

	@Before
	public void before() {
		def college =  mock(College)
		when(college.paymentGatewayAccount).thenReturn('ishGroup_Dev')
		when(college.paymentGatewayPass).thenReturn('test1234')

		paymentIn =  mock(PaymentIn)
		when(paymentIn.amount).thenReturn(new Money(20, 20))
		when(paymentIn.creditCardCVV) .thenReturn('321')
		when(paymentIn.creditCardName).thenReturn('john smith')
		when(paymentIn.creditCardNumber).thenReturn('5431111111111111' )
		when(paymentIn.creditCardExpiry).thenReturn('11/27')
		when(paymentIn.creditCardType).thenReturn(CreditCardType.VISA)
		when(paymentIn.college).thenReturn(college)
		when(paymentIn.clientReference).thenReturn('W123456')

		model =  mock(PaymentInModel)
		when(model.paymentIn).thenReturn(paymentIn)
	}


	@Test
	def void testRealGateway(){
		def service = new GatewayHelper()
		DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, (String) null).build()
		ProcessDPSResponse.valueOf(model, service.submitRequest(request)).process()
		checkSuccess()
	}

	@Test
	def void testException5AttemptsGetStatusFail() {
		def stub = new PaymentExpressWSSoap12Stub() {
			def counter = 0
			TransactionResult2 result2
			def TransactionResult2 submitTransaction(String postUsername,  String postPassword, TransactionDetails transactionDetails) {
				throw new RemoteException()
			}
			def  TransactionResult2 getStatus(String postUsername, String postPassword, String txnRef) {
				counter++
				if (counter < 5) {
					result2 = mock(TransactionResult2)
					when(result2.statusRequired).thenReturn('1')
					return result2
				} else {
					result2 = mock(TransactionResult2)
					when(result2.statusRequired).thenReturn('0')
					when(result2.retry).thenReturn('0')
					when(result2.authorized).thenReturn('0')
					return result2
				}
			}
		}

		def service = getCustomService(stub)

		when(paymentIn.status).thenReturn(FAILED_CARD_DECLINED)
		DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, (String) null).build()
		ProcessDPSResponse.valueOf(model, service.submitRequest(request)).process()
		checkFail()
	}

	@Test
	def void testStatusRequired5AttemptsGetStatusSuccess() {
		def stub = new PaymentExpressWSSoap12Stub() {
			def counter = 0
			TransactionResult2 result2
			def TransactionResult2 submitTransaction(String postUsername,  String postPassword, TransactionDetails transactionDetails) {
				result2 = mock(TransactionResult2)
				when(result2.statusRequired).thenReturn('1')
				return result2
			}
			def  TransactionResult2 getStatus(String postUsername, String postPassword, String txnRef) {
				counter++
				if (counter < 5) {
					throw new RemoteException()
				} else {
					result2 = mock(TransactionResult2)
					when(result2.statusRequired).thenReturn('0')
					when(result2.retry).thenReturn('0')
					when(result2.authorized).thenReturn('1')
					when(result2.dateSettlement).thenReturn('20161010')
					when(result2.dpsBillingId).thenReturn('billingId')
					when(result2.responseText).thenReturn('responseText')
					when(result2.txnRef).thenReturn('txnRef')
					return result2
				}
			}
		}

		def service = getCustomService(stub)

		DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, (String) null).build()
		ProcessDPSResponse.valueOf(model, service.submitRequest(request)).process()
		checkSuccess()
	}

	@Test
	void test5TimesRetry() {
		def stub = new PaymentExpressWSSoap12Stub() {
			TransactionResult2 result2
			TransactionResult2 submitTransaction(String postUsername,  String postPassword, TransactionDetails transactionDetails) {
				result2 = mock(TransactionResult2)
				when(result2.retry).thenReturn('1')
				return result2
			}
		}

		def service = getCustomService(stub)
		when(paymentIn.status).thenReturn(FAILED_CARD_DECLINED)
		DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, (String) null).build()
		ProcessDPSResponse.valueOf(model, service.submitRequest(request)).process()
		checkFail()
	}

	private void checkSuccess() {
		verify(paymentIn, times(1)).setStatus(SUCCESS)
		verify(paymentIn, times(1)).setBillingId(any(String))
		verify(paymentIn, times(1)).setGatewayResponse(any(String))
		verify(paymentIn, times(1)).setGatewayReference(any(String))
		verify(paymentIn, times(1)).setStatusNotes(SUCCESS_PAYMENT_IN)
		verify(paymentIn, times(1)).setDateBanked(any(Date))
	}

	private void checkFail() {
		verify(paymentIn, times(1)).setStatus(FAILED_CARD_DECLINED)
		verify(paymentIn, times(0)).setBillingId(any(String))
		verify(paymentIn, times(0)).setGatewayResponse(any(String))
		verify(paymentIn, times(0)).setGatewayReference(any(String))
		verify(paymentIn, times(1)).setStatusNotes(FAILED_PAYMENT_IN)
		verify(paymentIn, times(0)).setDateBanked(any(Date))
	}

	private GatewayHelper getCustomService(PaymentExpressWSSoap12Stub stub) {
		def service = new  GatewayHelper() {
			@Override
			def PaymentExpressWSSoap12Stub soapClientStub() {
				return stub
			}
			@Override
			def DPSResponse processDPSRequest(DPSRequest dpsRequest) {
				return super.processDPSRequest(dpsRequest)
			}
		}
		return service
	}
}
