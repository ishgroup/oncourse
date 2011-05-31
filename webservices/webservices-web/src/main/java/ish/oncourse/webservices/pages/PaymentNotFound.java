package ish.oncourse.webservices.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

public class PaymentNotFound implements ExceptionReporter {
	
	@Property
    private Throwable exception;
	
	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}
}
