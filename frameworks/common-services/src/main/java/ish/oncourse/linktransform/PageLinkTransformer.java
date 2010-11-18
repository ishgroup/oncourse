/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.linktransform;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.tag.ITagService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final String DIGIT_REGEXP = "(\\d+)";

	private static final Logger LOGGER = Logger
			.getLogger(PageLinkTransformer.class);

	/**
	 * courses/arts/drama Show course list page, optionally filtered by the
	 * subject tag identified by arts -> drama
	 */
	private static final Pattern COURSES_PATTERN = Pattern
			.compile("/courses(/(.+)+)*");

	/**
	 * course/ABC Show course detail for the cource with code ABC
	 */
	private static final Pattern COURSE_PATTERN = Pattern
			.compile("/course/((\\w|\\s)+)");

	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	private static final Pattern CLASS_PATTERN = Pattern
			.compile("/class/((\\w|\\s)+)-((\\w|\\s)+)");

	/**
	 * page/123 This is always available for every webpage, even if it doesn't
	 * have a URL alias
	 */
	private static final Pattern PAGENUM_PATTERN = Pattern
			.compile("/page/(\\d+)");

	/**
	 * sites Show the site list for all sites
	 */
	private static final Pattern SITES_PATTERN = Pattern.compile("/sites");

	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	private static final Pattern SITE_PATTERN = Pattern.compile("/site/(\\d+)");

	/**
	 * room/200 Show the room detail for the room with angel id of 200
	 */
	private static final Pattern ROOM_PATTERN = Pattern.compile("/room/(\\d+)");

	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	private static final Pattern TUTOR_PATTERN = Pattern
			.compile("/tutor/(\\d+)");

	/**
	 * /sitemap.xml Google specific sitemap file.
	 */
	private static final Pattern SITEMAP_PATTERN = Pattern
			.compile("/sitemap\\.xml");

	String[] IMMUTABLE_PATHS = new String[] {"/assets",  "/login", "/editpage", "/newpage", "/menubuilder", "/pageoptions", 
				"/ma.", "/site", "/sitesettings", "/pagetypes", "/menus", "/pages", "/blocks", "/blockedit"};
	
	
	private static final String HOME_PAGE_PATH = "/";

	/**
	 * Path of the search autocomplete
	 */
	private static final String ADVANCED_KEYWORD_PATH = "/advanced/keyword";

	/**
	 * Path of the suburbs autocomplete
	 */
	private static final String ADVANCED_SUBURB_PATH = "/advanced/suburbs";

	/**
	 * Path of the removing from cookies request
	 */
	private static final String REMOVE_FROM_COOKIES_PATH = "/removeFromCookies";

	/**
	 * Path of the adding to cookies request
	 */
	private static final String ADD_TO_COOKIES_PATH = "/addToCookies";

	/**
	 * Path of the refreshing the shortlist control
	 */
	private static final String REFRESH_SHORT_LIST_CONTROL_PATH = "/refreshShortListControl";

	/**
	 * Path of the refreshing the shortlist
	 */
	private static final String REFRESH_SHORT_LIST_PATH = "/refreshShortList";

	/**
	 * /Timeline/sessions?ids=123,456 Show the timeline view for the sessions of
	 * courseClasses with ids of 123 and 456
	 */
	private static final String TIMELINE_PATH = "/Timeline/sessions";

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

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {

		final String path = request.getPath().toLowerCase();

		for (String p : IMMUTABLE_PATHS) {
			if (path.startsWith(p)) {
				return null;
			}
		}

		if (HOME_PAGE_PATH.equals(path)) {
			request.setAttribute(IWebNodeService.PAGE_PATH_PARAMETER, "/");
			return new PageRenderRequestParameters("ui/Page",
					new EmptyEventContext(), false);
		}

		Matcher matcher;

		LOGGER.info("Rewrite InBound: path is: " + path);

		/*
		 * For speed these are currently ordered from most likely to be
		 * encountered, to least.
		 */

		matcher = COURSES_PATTERN.matcher(path);
		if (matcher.matches()) {
			String tagsPath = path.replaceFirst("/courses", "");
			if (tagsPath.startsWith("/")) {
				tagsPath = tagsPath.replaceFirst("/", "");
			}
			if (!tagsPath.equals("")) {
				if (tagsPath.endsWith("/")) {
					tagsPath = tagsPath.substring(0, tagsPath.length() - 1);
				}
				String tags[] = tagsPath.split("/");
				Tag rootTag = tagService.getRootTag();
				for (String tag : tags) {
					tag = tag.replaceAll("[+]", " ").replaceAll("[|]", "/");
					if (rootTag.hasChildWithName(tag)) {
						rootTag = tagService.getSubTagByName(tag);
					} else {
						return new PageRenderRequestParameters(
								"ui/PageNotFound", new EmptyEventContext(),
								false);
					}
				}
				request.setAttribute(Course.COURSE_TAG, rootTag);
			}
			return new PageRenderRequestParameters("ui/Courses",
					new EmptyEventContext(), false);
		}

		matcher = COURSE_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseCode", courseCode);
			return new PageRenderRequestParameters("ui/CourseDetails",
					new EmptyEventContext(), false);
		}

		matcher = CLASS_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseClassCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseClassCode", courseClassCode);
			return new PageRenderRequestParameters("ui/CourseClassDetails",
					new EmptyEventContext(), false);
		}

		matcher = PAGENUM_PATTERN.matcher(path);
		if (matcher.matches()) {
			String nodeNumber = matcher.group(1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER,
						nodeNumber);
				return new PageRenderRequestParameters("ui/Page",
						new EmptyEventContext(), false);
			}
		}

		matcher = SITES_PATTERN.matcher(path);
		if (matcher.matches()) {
			return new PageRenderRequestParameters("ui/Sites",
					new EmptyEventContext(), false);
		}

		matcher = SITE_PATTERN.matcher(path);
		if (matcher.matches()) {
			String siteId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("siteId", siteId);
			return new PageRenderRequestParameters("ui/SiteDetails",
					new EmptyEventContext(), false);
		}

		matcher = ROOM_PATTERN.matcher(path);
		if (matcher.matches()) {
			String roomId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("roomId", roomId);
			return new PageRenderRequestParameters("ui/RoomDetails",
					new EmptyEventContext(), false);
		}

		matcher = TUTOR_PATTERN.matcher(path);
		if (matcher.matches()) {
			String tutorId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("tutorId", tutorId);
			return new PageRenderRequestParameters("ui/TutorDetails",
					new EmptyEventContext(), false);
		}

		matcher = SITEMAP_PATTERN.matcher(path);
		if (matcher.matches()) {
			return new PageRenderRequestParameters("ui/SitemapXML",
					new EmptyEventContext(), false);
		}

		if (ADVANCED_KEYWORD_PATH.equals(path)) {
			return new PageRenderRequestParameters("ui/QuickSearchView",
					new EmptyEventContext(), false);
		}

		if (ADVANCED_SUBURB_PATH.equals(path)) {
			return new PageRenderRequestParameters("ui/SuburbsTextArray",
					new EmptyEventContext(), false);
		}

		if (ADD_TO_COOKIES_PATH.equalsIgnoreCase(path)) {
			String addedCourseClassId = request
					.getParameter(CourseClass.COURSE_CLASS_ID_PARAMETER);
			if (addedCourseClassId != null
					&& addedCourseClassId.matches(DIGIT_REGEXP)) {
				List<CourseClass> courseClasses = courseClassService
						.loadByIds(addedCourseClassId);
				if (!courseClasses.isEmpty()) {
					cookiesService.appendValueToCookieCollection(
							CourseClass.SHORTLIST_COOKEY_KEY,
							addedCourseClassId);
				}
			}
			return null;
		}

		if (REMOVE_FROM_COOKIES_PATH.equalsIgnoreCase(path)) {
			String removedCourseClassId = request
					.getParameter(CourseClass.COURSE_CLASS_ID_PARAMETER);
			if (removedCourseClassId != null
					&& removedCourseClassId.matches(DIGIT_REGEXP)) {
				List<CourseClass> courseClasses = courseClassService
						.loadByIds(removedCourseClassId);
				if (!courseClasses.isEmpty()) {
					cookiesService.removeValueFromCookieCollection(
							CourseClass.SHORTLIST_COOKEY_KEY,
							removedCourseClassId);
				}
			}
			return null;
		}

		if (REFRESH_SHORT_LIST_PATH.equalsIgnoreCase(path)) {
			return new PageRenderRequestParameters("ui/ShortListPage",
					new EmptyEventContext(), false);
		}

		if (REFRESH_SHORT_LIST_CONTROL_PATH.equalsIgnoreCase(path)) {
			return new PageRenderRequestParameters("ui/ShortListControlPage",
					new EmptyEventContext(), false);
		}

		if (TIMELINE_PATH.equalsIgnoreCase(path)) {
			return new PageRenderRequestParameters("ui/TimelineData",
					new EmptyEventContext(), false);
		}

		String nodePath = path;

		if (nodePath.endsWith("/")) {
			nodePath = nodePath.substring(0, nodePath.length() - 1);
		}
		if (webNodeService.isNodeExist(nodePath)) {
			request.setAttribute(WebNodeService.PAGE_PATH_PARAMETER, path);
			return new PageRenderRequestParameters("ui/Page",
					new EmptyEventContext(), false);
		}

		return new PageRenderRequestParameters("ui/PageNotFound",
				new EmptyEventContext(), false);
	}

	public Link transformPageRenderLink(Link defaultLink,
			PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
