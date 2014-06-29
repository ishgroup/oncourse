package ish.oncourse.util;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.model.Tag;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.services.Request;

public class HTMLUtils {
	private static final Logger LOGGER = Logger.getLogger(HTMLUtils.class);

    public static final String USER_AGENT_HEADER = "User-Agent";

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

	public static String getCanonicalLinkPathFor(Product product, Request request)
	{
		return HTMLUtils.HTTP_PROTOCOL + request.getServerName() +
				String.format("%s/product/%s", request.getContextPath(), product.getSku().toUpperCase());
	}


	public static String getCanonicalLinkPathForCourses(Request request, Tag browseTag)
	{
		return HTMLUtils.HTTP_PROTOCOL + request.getServerName() + request.getContextPath() + (browseTag == null ? request.getPath(): browseTag.getLink().toLowerCase());

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

	public static String cutDescription(ITextileConverter textileConverter, IPlainTextExtractor plainTextExtractor,
											   String details, int size)
	{
		try {
			String detail = textileConverter.convertCustomTextile(details,
					new ValidationErrors());
			if (detail != null) {
				String plainText = plainTextExtractor.extractFromHtml(detail);
				return StringUtils.abbreviate(plainText, size);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
		return StringUtils.EMPTY;
	}

}
