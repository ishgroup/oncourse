package ish.oncourse.util;

import org.apache.tapestry5.services.Request;

public class HTMLUtils {

	public static final String VALUE_on = "on";
	public static final String HTTP_PROTOCOL = "http://";


	public static String getUrlBy(Request request, Class pageClass)
	{
		return String.format("%s%s/%s", HTMLUtils.HTTP_PROTOCOL, request.getServerName(), pageClass.getSimpleName());

	}
	public static boolean parserBooleanValue(String value)
	{
		if (value == null)
			return false;

		if (value.equalsIgnoreCase(VALUE_on))
			return true;
		return Boolean.valueOf(value);
	}
}
