/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.website.linktransforms;

import ish.oncourse.services.node.IWebNodeService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

/**
 * @author marek
 */
public class PageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger LOGGER = Logger.getLogger(PageLinkTransformer.class);

	/**
	 * courses/arts/drama Show course list page, optionally filtered by the subject tag identified by arts -> drama
	 */
	private static final Pattern COURSES_PATTERN = Pattern.compile("/courses(/)?(\\w+)?");

	/**
	 * course/ABC Show course detail for the cource with code ABC
	 */
	private static final Pattern COURSE_PATTERN = Pattern.compile("/course/((\\w|\\s)+)");

	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	private static final Pattern CLASS_PATTERN = Pattern.compile("/class/((\\w|\\s)+)-((\\w|\\s)+)");

	/**
	 * page/123 This is always available for every webpage, even if it doesn't have a URL alias
	 */
	private static final Pattern PAGENUM_PATTERN = Pattern.compile("/page/(\\d+)");

	/**
	 * sites Show the site list for all sites
	 */
	private static final Pattern SITES_PATTERN = Pattern.compile("/sites");

	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	private static final Pattern SITE_PATTERN = Pattern.compile("/site(/(\\d+))?");

	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	private static final Pattern TUTOR_PATTERN = Pattern.compile("/tutor/(\\d+)");

	private static final String HOME_PAGE_PATH = "/";

	private static final String ASSETS_PATH = "/assets";

	@Inject
	PageRenderLinkSource pageRenderLinkSource;

	@Inject
	TypeCoercer typeCoercer;

	@Inject
	RequestGlobals requestGlobals;

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		final String path = request.getPath();
		// TODO remove checking on servlets because it will be replaced with
		// assets module
		if (HOME_PAGE_PATH.equals(path) || path.startsWith(ASSETS_PATH) || path.startsWith("/servlet")) {
			return null;
		}

		Matcher matcher;

		LOGGER.info("Rewrite InBound: path is: " + path);

		/*
		 * These are currently ordered from most likely to be encountered, to least
		 */

		matcher = COURSES_PATTERN.matcher(path);

		if (matcher.matches()) {

			return new PageRenderRequestParameters("ui/Courses", new EmptyEventContext(), false);
		}

		matcher = COURSE_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseCode", courseCode);
			return new PageRenderRequestParameters("ui/CourseDetails", new EmptyEventContext(), false);
		}

		matcher = CLASS_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseClassCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseClassCode", courseClassCode);
			return new PageRenderRequestParameters("ui/CourseClassDetails", new EmptyEventContext(), false);
		}

		matcher = PAGENUM_PATTERN.matcher(path);
		if (matcher.matches()) {
			String nodeNumber = matcher.group(1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
				return new PageRenderRequestParameters("ui/Page", new EmptyEventContext(), false);
			}
		}

		matcher = SITES_PATTERN.matcher(path);
		if (matcher.matches()) {
			throw new NotImplementedException("sites");
		}

		matcher = SITE_PATTERN.matcher(path);
		if (matcher.matches()) {
			throw new NotImplementedException("site");
		}

		matcher = TUTOR_PATTERN.matcher(path);
		if (matcher.matches()) {
			throw new NotImplementedException("tutor");
		}

		// If we match no other pattern we need to look up the page in the list
		// of URL aliases
		throw new NotImplementedException("URL alias");
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
