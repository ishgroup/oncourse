package ish.oncourse.util.payment;

public interface IPaymentProcessControllerBuilder {
	PaymentProcessController build(final String sessionId);
	boolean isNeedResetOldSessionController(PaymentProcessController paymentProcessController, String sessionId);
}
