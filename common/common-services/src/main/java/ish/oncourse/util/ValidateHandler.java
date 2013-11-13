package ish.oncourse.util;

import java.util.HashMap;
import java.util.Map;

public class ValidateHandler {

	public static final String HTML_CLASS_validate = "validate has-error";
	public static final String HTML_CLASS_valid = "valid";

	public static final String HTML_CLASS_error = "t-error";

	private Map<String,String> errors = new HashMap<>();

	public String error(String fieldName)
	{
		return errors.get(fieldName);
	}

	public String validateClass(String fieldName)
	{
		if (errors.containsKey(fieldName))
			return HTML_CLASS_validate;
		else
			return HTML_CLASS_valid;
	}

	public String errorClass(String fieldName)
	{
		return errors.containsKey(fieldName) ? HTML_CLASS_error: FormatUtils.EMPTY_STRING;
	}


	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public Map<String, String> getErrors()
	{
		return errors;
	}
}
