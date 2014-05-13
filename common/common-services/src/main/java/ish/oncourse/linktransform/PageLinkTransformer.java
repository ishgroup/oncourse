/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.linktransform;

import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.room.IRoomService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.commons.lang.StringUtils;
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

import javax.servlet.http.HttpServletResponse;

public class PageLinkTransformer implements PageRenderLinkTransformer {
	
	private static final String REMOVE_ITEM_ID_PARAMETER = "removeItemId";
	private static final String ADD_ITEM_ID_PARAMETER = "addItemId";
	private static final String KEY_PARAMETER = "key";
	private static final String TUTOR_ID_ATTRIBUTE = "tutorId";
	private static final String DIGIT_PATTERN = "\\d+";
	private static final String CMS_PATH = "/cms";
	private static final String COURSES_PATH = "/courses";
	private static final String LEFT_SLASH_CHARACTER = "/";
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PageLinkTransformer.class);
	
	
	/**
	 * Special reseverved path for system pages, we do not treat them as webnode nor can have the webnode page with such path.
	 */
	public static String[] IMMUTABLE_PATHS = new String[] { "/assets", "/login", "/editpage", "/newpage", "/menubuilder", "/pageoptions",
			"/ma.", "/site", "/sitesettings", "/pagetypes", "/menus", "/pages", "/blocks", "/blockedit", "/site.blocks.",
			"/site.pagetypes.", "ish/internal/autocomplete.sub", "/pt.sort", "ui/textileform.send", "/ui/timezoneholder.", "/webdav"};

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
	
	@Inject
	IVoucherService voucherService;

	@Inject
	ISitesService sitesService;

	@Inject
	IRoomService roomService;

	@Inject
	private IWebSiteService webSiteService;

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {

		final String path = request.getPath().toLowerCase();
		LOGGER.info("Rewrite InBound: path is: " + path);

		PageIdentifier pageIdentifier = PageIdentifier.getPageIdentifierByPath(path);

        /**
         * ISHHealthCheck can be used without college keyCode.
         */
		if (webSiteService.getCurrentWebSite() == null && pageIdentifier != PageIdentifier.ISHHealthCheck) {
			requestGlobals.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new PageRenderRequestParameters(PageIdentifier.SiteNotFound.getPageName(), new EmptyEventContext(), false);
		}

		switch (pageIdentifier) {
		case Home:
			request.setAttribute(IWebNodeService.PAGE_PATH_PARAMETER, LEFT_SLASH_CHARACTER);
			if (webNodeService.getCurrentNode() == null) {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Products:
			break;
		case Courses:
			final boolean isCMSCoursesSearch = requestGlobals.getHTTPServletRequest().getRequestURI().toLowerCase()
					.startsWith(CMS_PATH + COURSES_PATH);
			
			String tagsPath = requestGlobals.getHTTPServletRequest().getRequestURI().toLowerCase()
					.replaceFirst(isCMSCoursesSearch ? CMS_PATH + COURSES_PATH : COURSES_PATH, StringUtils.EMPTY);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("tagsPath:%s", tagsPath));
			}
			
			if (!tagsPath.startsWith(CMS_PATH)) {
				if (tagsPath.startsWith(LEFT_SLASH_CHARACTER)) {
					tagsPath = tagsPath.replaceFirst(LEFT_SLASH_CHARACTER, StringUtils.EMPTY);
				}
				if (!tagsPath.equals(StringUtils.EMPTY)) {
					Tag tag = tagService.getTagByFullPath(tagsPath);

					if (tag == null) {
						pageIdentifier = PageIdentifier.PageNotFound;
						break;
					}
					request.setAttribute(Course.COURSE_TAG, tag);
				}
			}
			break;
		case Product:
			Product product = null;
			String productSKU = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (productSKU != null) {
				product = voucherService.loadAvailableVoucherProductBySKU(productSKU);
			}
			if (product != null) {
				request.setAttribute(Product.class.getSimpleName(), product);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Course:
			Course course = null;
			String courseCode = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (courseCode != null) {
				course = courseService.getCourse(Course.CODE_PROPERTY, courseCode);
			}
			if (course != null) {
				request.setAttribute(Course.class.getSimpleName(), course);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case CourseClass:
			CourseClass courseClass = null;
			String courseClassCode = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (courseClassCode != null) {
				courseClass = courseClassService.getCourseClassByFullCode(courseClassCode);
			}
			if (courseClass != null) {
				request.setAttribute(CourseClass.class.getSimpleName(), courseClass);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Page:
			String nodeNumber = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
				if (webNodeService.getCurrentNode() == null) {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Sites:
			break;
		case Site:
			Site site = null;
			String siteId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (siteId != null && siteId.matches(DIGIT_PATTERN)) {
				site = sitesService.getSite(Site.ANGEL_ID_PROPERTY, siteId);
			}
			if (site != null) {
				request.setAttribute(Site.class.getSimpleName(), site);
			} else {
				return new PageRenderRequestParameters(PageIdentifier.PageNotFound.getPageName(), new EmptyEventContext(), false);
			}
			break;
		case Room:
			Room room = null;
			String roomId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (roomId != null && roomId.matches(DIGIT_PATTERN)) {
				room = roomService.getRoom(Room.ANGEL_ID_PROPERTY, Long.valueOf(roomId));
			}
			if (room != null) {
				request.setAttribute(Room.class.getSimpleName(), room);
			} else {
				pageIdentifier = PageIdentifier.PageNotFound;
			}
			break;
		case Tutor:
			String tutorId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			request.setAttribute(TUTOR_ID_ATTRIBUTE, tutorId);
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
		case CoursesSitesMap:
			break;
		}

		if (pageIdentifier != PageIdentifier.PageNotFound) {
			return new PageRenderRequestParameters(pageIdentifier.getPageName(), new EmptyEventContext(), false);
		}

		if (ADD_TO_COOKIES_PATH.equalsIgnoreCase(path) || REMOVE_FROM_COOKIES_PATH.equalsIgnoreCase(path)) {
			boolean isAddAction = ADD_TO_COOKIES_PATH.equalsIgnoreCase(path);
			String key = request.getParameter(KEY_PARAMETER);
			String value = isAddAction ? request.getParameter(ADD_ITEM_ID_PARAMETER) : request.getParameter(REMOVE_ITEM_ID_PARAMETER);
			if (key != null && value != null) {
				if (value.matches("(\\d+)")) {

					if (isAddAction) {
						cookiesService.appendValueToCookieCollection(key, value);
					} else {
						cookiesService.removeValueFromCookieCollection(key, value);
					}
				}
				if (key.equalsIgnoreCase(CourseClass.SHORTLIST_COOKIE_KEY) || key.equalsIgnoreCase(Product.SHORTLIST_COOKIE_KEY)) {
					return new PageRenderRequestParameters(PageIdentifier.Shortlist.getPageName(), new EmptyEventContext(), false);
				}
				if (key.equalsIgnoreCase(Discount.PROMOTIONS_KEY)) {
					return new PageRenderRequestParameters(PageIdentifier.Promotions.getPageName(), new EmptyEventContext(), false);
				}
			}

			return null;
		}

		String nodePath = path;

		if (nodePath.endsWith(LEFT_SLASH_CHARACTER)) {
			nodePath = nodePath.substring(0, nodePath.length() - 1);
		}
		if (webNodeService.getNodeForNodePath(nodePath) != null) {
			request.setAttribute(IWebNodeService.PAGE_PATH_PARAMETER, path);
			return new PageRenderRequestParameters(PageIdentifier.Page.getPageName(), new EmptyEventContext(), false);
		}

		for (String p : IMMUTABLE_PATHS) {
			if (path.startsWith(p)) {
				return null;
			}
		}

		if (requestGlobals.getHTTPServletRequest() != null
				&& requestGlobals.getHTTPServletRequest().getContextPath().equalsIgnoreCase(CMS_PATH)) {
			if ("y".equalsIgnoreCase(requestGlobals.getRequest().getParameter("newpage"))) {
				// return just ordinary page for cms to give it the ability to
				// create the "new page"
				return new PageRenderRequestParameters(PageIdentifier.Page.getPageName(), new EmptyEventContext(), false);
			}
		}

		requestGlobals.getResponse().setStatus(404);

		return new PageRenderRequestParameters(PageIdentifier.PageNotFound.getPageName(), new EmptyEventContext(), false);
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
