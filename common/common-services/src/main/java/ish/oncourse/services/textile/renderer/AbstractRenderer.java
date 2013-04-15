package ish.oncourse.services.textile.renderer;

import java.util.Map;

import ish.oncourse.services.textile.validator.IValidator;
import ish.oncourse.util.ValidationErrors;

public abstract class AbstractRenderer implements IRenderer {
	protected static final String YES_REQUIRED_PARAM_VALUE = "yes";
	protected IValidator validator;

	public String render(String tag, ValidationErrors errors) {
		validator.validate(tag, errors);
		return tag;
	}
	
	/**
	 * Parse potentially boolean attribute value.
	 * @param params - parameters map
	 * @param attributeValue - attribute value for check
	 * @return true if attributeValue match yes or true ignore cases.
	 */
	protected static boolean parseBooleanTextileAttribute(Map<String, String> params, String attributeValue) {
		String paramStringValue = params.get(attributeValue);
		return Boolean.getBoolean(paramStringValue.toLowerCase()) || YES_REQUIRED_PARAM_VALUE.equals(paramStringValue.toLowerCase());
	}
}
