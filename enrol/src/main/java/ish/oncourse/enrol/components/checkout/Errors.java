package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.ValidationResult;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Errors {

	@Parameter(required = true)
	@Property
	private ValidationResult validationResult;

    @Property
	private String error;

    @Property
    private String warning;

}
