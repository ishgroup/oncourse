package ish.oncourse.webservices.components;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Form;

public class ErrorDisplay {

	@Parameter
	private Form form;

	@Parameter
	private Field field;

	public String getError() {
		ValidationTracker validationTracker = form.getDefaultTracker();
		return (validationTracker != null && validationTracker.getError(field) != null) ? validationTracker.getError(field) : "";
	}
}
