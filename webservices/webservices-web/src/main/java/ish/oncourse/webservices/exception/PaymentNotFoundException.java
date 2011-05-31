package ish.oncourse.webservices.exception;

public class PaymentNotFoundException extends RuntimeException {
	public PaymentNotFoundException(String message) {
		super(message);
	}
}
