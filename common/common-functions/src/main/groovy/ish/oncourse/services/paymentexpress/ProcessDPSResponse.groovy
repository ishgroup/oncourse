package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.common.types.PaymentStatus
import ish.oncourse.model.PaymentOut
import ish.oncourse.util.payment.PaymentInFail
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInSucceed
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.services.paymentexpress.DPSResponse.ResultStatus.*

@CompileStatic
class ProcessDPSResponse {

	private PaymentInModel model
	private DPSResponse response
	private PaymentOut paymentOut
	private ProcessCase processCase


	static ProcessDPSResponse valueOf(PaymentInModel model, DPSResponse response) {
		new ProcessDPSResponse(model: model, response: response,
				processCase: new ProcessCase() {
					@Override
					void onSUCCESS() {
						if (StringUtils.trimToNull(response.result2.dpsBillingId)) {
							model.paymentIn.setBillingId(response.result2.dpsBillingId)
						}
						model.paymentIn.setGatewayResponse(response.result2.responseText)
						model.paymentIn.setGatewayReference(response.result2.dpsTxnRef)
						model.paymentIn.setStatusNotes(IPaymentGatewayService.SUCCESS_PAYMENT_IN);
						PaymentInSucceed.valueOf(model).perform();
						model.paymentIn.setDateBanked(PaymentExpressUtil.translateSettlementDate(response.result2.dateSettlement));
					}

					@Override
					void onFAILED() {
						model.paymentIn.setStatusNotes(IPaymentGatewayService.FAILED_PAYMENT_IN);
						model.paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED)
						PaymentInFail.valueOf(model).perform()
						
		
					}
		
					@Override
					void onUNKNOWN() {
						model.paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
						model.paymentIn.setStatusNotes(IPaymentGatewayService.UNKNOW_RESULT_PAYMENT_IN);
					}
		})		 
	}

	static ProcessDPSResponse valueOf(PaymentOut paymentOut, DPSResponse response) {
		new ProcessDPSResponse(paymentOut: paymentOut, response: response,
				processCase:  new ProcessCase() {
					@Override
					void onSUCCESS() {
						paymentOut.setStatusNotes(IPaymentGatewayService.SUCCESS_PAYMENT_OUT)
						paymentOut.succeed()
						paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(response.result2.dateSettlement))
						paymentOut.setDatePaid(new Date())
					}

					@Override
					void onFAILED() {
						paymentOut.setStatusNotes(IPaymentGatewayService.FAILED_PAYMENT_OUT)
						paymentOut.setStatus(PaymentStatus.FAILED_CARD_DECLINED)
						paymentOut.failed();
					}

					@Override
					void onUNKNOWN() {
						paymentOut.setStatusNotes(IPaymentGatewayService.FAILED_PAYMENT_OUT_NULL_RESPONSE)
						paymentOut.failed()
					}
			})
	}

	
	def process() {
		
		switch (response.status) {
			case SUCCESS:
				processCase.onSUCCESS()
				break
			case RETRY:
			case FAILED:
				processCase.onFAILED()
				break
			case UNKNOWN:
				processCase.onUNKNOWN()
				break;
			default: throw new IllegalArgumentException('Unexpected result status')
		}
	}

	interface ProcessCase {
		void onSUCCESS()
		void onFAILED()
		void onUNKNOWN()
	}
}
