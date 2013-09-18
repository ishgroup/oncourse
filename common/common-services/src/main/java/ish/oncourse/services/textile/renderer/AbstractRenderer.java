package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.IValidator;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public abstract class AbstractRenderer implements IRenderer {
	protected static final String YES_REQUIRED_PARAM_VALUE = "yes";
	protected IValidator validator;
	private ValidationErrors errors = new ValidationErrors();

	/**
	 * Parse potentially boolean attribute value.
	 *
	 * @param params         - parameters map
	 * @param attributeValue - attribute value for check
	 * @return true if attributeValue match yes or true ignore cases.
	 */
	protected static boolean parseBooleanTextileAttribute(Map<String, String> params, String attributeValue) {
		String paramStringValue = params.get(attributeValue);
		if (StringUtils.trimToNull(paramStringValue) != null) {
			paramStringValue = paramStringValue.toLowerCase();
		}
		return Boolean.getBoolean(paramStringValue) || YES_REQUIRED_PARAM_VALUE.equals(paramStringValue);
	}

	public String render(String tag) {
		validator.validate(tag, errors);
		String result = tag;
		if (!errors.hasFailures())
			result = internalRender(tag);
		result = handleErrors(tag, result);
		return result;
	}

	protected abstract String internalRender(String tag);

	protected String handleErrors(String tag, String result) {
		if (errors.hasFailures() || result == null)
			result = TextileUtil.getReplacementForSyntaxErrorTag(tag, errors);
		return result;
	}

	public ValidationErrors getErrors() {
		return errors;
	}
}
