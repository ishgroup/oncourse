/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.linktransform;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Tag;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.tag.ITagService;

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

public class PageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger LOGGER = Logger.getLogger(PageLinkTransformer.class);

	public static String[] IMMUTABLE_PATHS = new String[] { "/assets", "/login", "/editpage",
			"/newpage", "/menubuilder", "/pageoptions", "/ma.", "/site", "/sitesettings",
			"/pagetypes", "/menus", "/pages", "/blocks", "/blockedit", "/site.blocks.",
			"/site.pagetypes.", "/ui/autocomplete.sub", "/pt.sort" };

	/**
	 * Path of the removing from cookies request
	 */
	private static final String REMOVE_FROM_COOKIES_PATH = "/removeFromCookies";

	/**
	 * Path of the adding to cookies request
	 */
	private static final String ADD_TO_COOKIES_PATH = "/addToCookies";

	@Inject
	PageRenderLinkSource pageRenderLinkSource;

	@Inject
	TypeCoercer typeCoercer;

	@Inject
	RequestGlobals requestGlobals;

	@Inject
	IWebNodeService webNodeService;

	@Inject
	ITagService tagService;

	@Inject
	ICookiesService cookiesService;

	@Inject
	ICourseClassService courseClassService;

	@Inject
	ICourseService courseService;

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {

		final String path = request.getPath().toLowerCase();
		LOGGER.info("Rewrite InBound: path is: " + path);

		PageIdentifier pageIdentifier = PageIdentifier.getPageIdentifierByPath(path);

		switch (pageIdentifier) {
		case Home:
			request.setAttribute(IWebNodeService.PAGE_PATH_PARAMETER, "/");
			break;
		case Courses:
			String tagsPath = path.replaceFirst("/courses", "");
			if (tagsPath.startsWith("/")) {
				tagsPath = tagsPath.replaceFirst("/", "");
			}
			if (!tagsPath.equals("")) {
				Tag tag = tagService.getTagByFullPath(tagsPath);
				if (tag == null) {
					pageIdentifier = PageIdentifier.PageNotFound;
					break;
				}
				request.setAttribute(Course.COURSE_TAG, tag);
			}
			break;
		case Course:
			Course course = null;
			String courseCode = path.substring(path.lastIndexOf("/") + 1);
			if (courseCode != null) {
				course = courseService.getCourse(Course.CODE_PROPERTY, courseCode);
			}
			if (course != null) {
				request.setAttribute("course", course);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case CourseClass:
			String courseClassCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseClassCode", courseClassCode);
			break;
		case Page:
			String nodeNumber = path.substring(path.lastIndexOf("/") + 1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Sites:
			break;
		case Site:
			String siteId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("siteId", siteId);
			break;
		case Room:
			String roomId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("roomId", roomId);
			break;
		case Tutor:
			String tutorId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("tutorId", tutorId);
			break;
		case Sitemap:
			break;
		case AdvancedKeyword:
			break;
		case AdvancedSuburbs:
			break;
		case Shortlist:
			break;
		case AddDiscount:
			break;
		case Promotions:
			break;
		case Timeline:
			break;
		}

		if (pageIdentifier != PageIdentifier.PageNotFound) {
			return new PageRenderRequestParameters(pageIdentifier.getPageName(),
					new EmptyEventContext(), false);
		}

		if (ADD_TO_COOKIES_PATH.equalsIgnoreCase(path)
				|| REMOVE_FROM_COOKIES_PATH.equalsIgnoreCase(path)) {
			boolean isAddAction = ADD_TO_COOKIES_PATH.equalsIgnoreCase(path);
			String key = request.getParameter("key");
			String value = isAddAction ? request.getParameter("addItemId") : request
					.getParameter("removeItemId");
			if (key != null && value != null) {
				if (value.matches("(\\d+)")) {

					if (isAddAction) {
						cookiesService.appendValueToCookieCollection(key, value);
					} else {
						cookiesService.removeValueFromCookieCollection(key, value);
					}
				}
				if (key.equalsIgnoreCase(CourseClass.SHORTLIST_COOKIE_KEY)) {
					return new PageRenderRequestParameters(PageIdentifier.Shortlist.getPageName(),
							new EmptyEventContext(), false);
				}
				if (key.equalsIgnoreCase(Discount.PROMOTIONS_KEY)) {
					return new PageRenderRequestParameters(PageIdentifier.Promotions.getPageName(),
							new EmptyEventContext(), false);
				}
			}

			return null;
		}

		String nodePath = path;

		if (nodePath.endsWith("/")) {
			nodePath = nodePath.substring(0, nodePath.length() - 1);
		}
		if (webNodeService.getNodeForNodePath(nodePath) != null) {
			request.setAttribute(WebNodeService.PAGE_PATH_PARAMETER, path);
			return new PageRenderRequestParameters(PageIdentifier.Page.getPageName(),
					new EmptyEventContext(), false);
		}

		for (String p : IMMUTABLE_PATHS) {
			if (path.startsWith(p)) {
				return null;
			}
		}

		return new PageRenderRequestParameters(PageIdentifier.PageNotFound.getPageName(),
				new EmptyEventContext(), false);
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
