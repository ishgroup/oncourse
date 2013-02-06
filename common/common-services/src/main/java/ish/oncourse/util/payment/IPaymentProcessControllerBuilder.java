package ish.oncourse.util.payment;

public interface IPaymentProcessControllerBuilder {
	PaymentProcessController build(final String sessionId);
}
