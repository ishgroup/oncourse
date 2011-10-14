package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class HintComponent {

	@Parameter
	@Property
	private String validationMessage;
	
	@Parameter
	@Property
	private String hint;
}
