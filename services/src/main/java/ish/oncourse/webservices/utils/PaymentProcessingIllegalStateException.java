package ish.oncourse.webservices.utils;

public class PaymentProcessingIllegalStateException extends RuntimeException {
    public PaymentProcessingIllegalStateException() {
        super();
    }

    public PaymentProcessingIllegalStateException(String s) {
        super(s);
    }

    public PaymentProcessingIllegalStateException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PaymentProcessingIllegalStateException(Throwable throwable) {
        super(throwable);
    }
}
