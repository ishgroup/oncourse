package ish.oncourse.util;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.model.Tag;
import ish.oncourse.services.environment.IEnvironmentService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;

public class HTMLUtils {

    public static final String USER_AGENT_HEADER = "User-Agent";

    public static final String VALUE_on = "on";
	public static final String HTTPS_PROTOCOL = "https://";

	public static String getUrlBy(String serverName, Class pageClass)
	{
		return String.format("%s%s/%s", HTMLUtils.HTTPS_PROTOCOL, serverName, pageClass.getSimpleName());
	}


	public static String getUrlBy(Request request, Class pageClass)
	{
		return String.format("%s%s/%s/%s", HTMLUtils.HTTPS_PROTOCOL, request.getServerName(), request.getContextPath(), pageClass.getSimpleName());
	}

	public static boolean parserBooleanValue(String value)
	{
		if (value == null)
			return false;

		if (value.equalsIgnoreCase(VALUE_on))
			return true;
		return Boolean.valueOf(value);
	}

	public static String getCanonicalRelativeLinkPath(Course course, Request request) {
		return String.format("%s/course/%s", request.getContextPath(), course.getCode());
	}

	public static String getCanonicalLinkPathFor(Course course, Request request) {
		  return HTMLUtils.HTTPS_PROTOCOL + request.getServerName() + getCanonicalRelativeLinkPath(course, request);
	}

	public static String getCanonicalLinkPathFor(Product product, Request request) {
		return HTMLUtils.HTTPS_PROTOCOL + request.getServerName() +
				String.format("%s/product/%s", request.getContextPath(), product.getSku());
	}

	public static String getCanonicalRelativeLinkPathForCourses(Request request, Tag browseTag) {
		return request.getContextPath() + (browseTag == null ? request.getPath() : browseTag.getLink());
	}

	public static String getCanonicalLinkPathForCourses(Request request, Tag browseTag) {
		return HTMLUtils.HTTPS_PROTOCOL + request.getServerName() + getCanonicalRelativeLinkPathForCourses(request, browseTag);
	}


	public static String getMetaGeneratorContent(IEnvironmentService environmentService) {
		StringBuilder buff = new StringBuilder(
				environmentService.getApplicationName());

		String buildServerID = environmentService.getBuildServerID();
		if (!StringUtils.isEmpty(buildServerID)) {
			buff.append(' ').append(buildServerID);
		}

		String scmVersion = environmentService.getScmVersion();
		if (!StringUtils.isEmpty(scmVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(scmVersion);
		}

		String ciVersion = environmentService.getCiVersion();
		if (!StringUtils.isEmpty(ciVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(ciVersion);
		}
		return buff.toString();
	}
}
