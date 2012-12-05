package ish.oncourse.enrol.components.checkout;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.Map;

public class Errors {

	@Parameter(required = true)
	@Property
	private Map<String,String> errors;

    @Parameter(required = true)
    @Property
    private Map<String,String> warnings;


    @Property
	private String error;

    @Property
    private String warning;

}
