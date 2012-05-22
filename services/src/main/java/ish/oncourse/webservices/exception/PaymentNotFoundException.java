package ish.oncourse.webservices.exception;

public class PaymentNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 4305743127744749495L;

	public PaymentNotFoundException(String message) {
		super(message);
	}
}
