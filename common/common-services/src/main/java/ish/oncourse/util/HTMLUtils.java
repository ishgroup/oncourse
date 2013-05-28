package ish.oncourse.util;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import org.apache.tapestry5.services.Request;

public class HTMLUtils {

	public static final String VALUE_on = "on";
	public static final String HTTP_PROTOCOL = "http://";

	public static String getUrlBy(String serverName, Class pageClass)
	{
		return String.format("%s%s/%s", HTMLUtils.HTTP_PROTOCOL, serverName, pageClass.getSimpleName());
	}


	public static String getUrlBy(Request request, Class pageClass)
	{
		return String.format("%s%s/%s/%s", HTMLUtils.HTTP_PROTOCOL, request.getServerName(),request.getContextPath(), pageClass.getSimpleName());
	}

	public static boolean parserBooleanValue(String value)
	{
		if (value == null)
			return false;

		if (value.equalsIgnoreCase(VALUE_on))
			return true;
		return Boolean.valueOf(value);
	}

	public static String getCanonicalLinkPathFor(Course course, Request request)
	{
		  return HTMLUtils.HTTP_PROTOCOL + request.getServerName() +
				String.format("%s/course/%s", request.getContextPath(), course.getCode().toUpperCase());

	}

	public static String getCanonicalLinkPathForCourses(Request request, Tag browseTag)
	{
		return HTMLUtils.HTTP_PROTOCOL + request.getServerName() + request.getContextPath() + (browseTag == null ? request.getPath(): browseTag.getDefaultPath().toLowerCase());

	}

}
