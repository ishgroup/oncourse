package ish.oncourse.ui.components;

import ish.oncourse.components.ISHCommon;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class HintComponent extends ISHCommon {

	@Parameter
	@Property
	private String validationMessage;
	
	@Parameter
	@Property
	private String hint;
}
