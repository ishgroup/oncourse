package ish.oncourse.webservices.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

@Import(stylesheet="css/screen.css")
public class ErrorPage implements ExceptionReporter {

	@Property
    private Throwable exception;
	
	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}
}
