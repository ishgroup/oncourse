package ish.oncourse.enrol.checkout;

public class HTMLUtils {

	public static final String VALUE_on = "on";

	public static boolean parserBooleanValue(String value)
	{
		if (value == null)
			return false;

		if (value.equalsIgnoreCase(VALUE_on))
			return true;
		return Boolean.valueOf(value);
	}
}
