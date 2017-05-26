package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.TransactionResult2
import groovy.transform.CompileStatic

@CompileStatic
class DPSResponse {
	TransactionResult2 result2
	ResultStatus status


	static DPSResponse valueOf(TransactionResult2 result2) {
		def response = new DPSResponse(result2: result2)
		
		if (!result2 || PaymentExpressUtil.translateFlag(result2.statusRequired)) {
			response.setStatus(ResultStatus.UNKNOWN)
		} else if (PaymentExpressUtil.translateFlag(result2.retry)) {
			response.setStatus(ResultStatus.RETRY)
		} else if (PaymentExpressUtil.translateFlag(result2.authorized)) {
			response.setStatus(ResultStatus.SUCCESS)
		} else {
			response.setStatus(ResultStatus.FAILED)
		}
		return response
	}

	static enum ResultStatus {
		SUCCESS,
		FAILED,
		UNKNOWN,
		RETRY
	}
	
}
