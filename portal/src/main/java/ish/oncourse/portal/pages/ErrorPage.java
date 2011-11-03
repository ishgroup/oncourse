package ish.oncourse.portal.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

public class ErrorPage implements ExceptionReporter {

	@Property
    private Throwable exception;
	
	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}
}
