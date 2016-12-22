package ish.oncourse.webservices.components;

import ish.oncourse.services.payment.GetPaymentState;

public class PaymentResult {

	private GetPaymentState.PaymentState state;

	public void setState(GetPaymentState.PaymentState state) {
		this.state = state;
	}

    public boolean isPaymentFailed() {
        return GetPaymentState.PaymentState.FAILED.equals(state);
 	}

	public boolean isPaymentSuccess() {
		return GetPaymentState.PaymentState.SUCCESS.equals(state);
	}
	
	public boolean isRetry() {
		return GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER.equals(state);
	}
	
	public boolean isNotProcessed() {
		return GetPaymentState.PaymentState.FILL_CC_DETAILS.equals(state);
    }

	public boolean isWrongPaymentExpressResult() {
		return GetPaymentState.PaymentState.DPS_ERROR.equals(state);
	}

}
