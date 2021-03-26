/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.linktransform;

import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.linktransform.functions.GetCourseByPath;
import ish.oncourse.linktransform.functions.GetCourseClassByPath;
import ish.oncourse.model.*;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.room.IRoomService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tutor.GetIsActiveTutor;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class PageLinkTransformer implements PageRenderLinkTransformer {

	public static final String ATTR_coursesTag = "coursesTag";

	private static final String REMOVE_ITEM_ID_PARAMETER = "removeItemId";
	private static final String ADD_ITEM_ID_PARAMETER = "addItemId";
	private static final String KEY_PARAMETER = "key";


	public static final String REQUEST_ATTR_redirectTo = "redirectTo";
	public static final String REQUEST_ATTR_redirectToParams = "redirectToParams";

	private static final String TUTOR_ATTRIBUTE = "tutor";
	private static final String DIGIT_PATTERN = "\\d+";
	private static final String CMS_PATH = "/cms";
	private static final String COURSES_PATH = "/courses";
	private static final String LEFT_SLASH_CHARACTER = "/";

	private static final String[] COOKIE_KEYS = {CourseClass.SHORTLIST_COOKIE_KEY, Product.SHORTLIST_COOKIE_KEY, Product.SHORTLIST_COOKIE_KEY};

	/**
	 * Logger.
	 */
	private static final Logger logger = LogManager.getLogger();


	/**
	 * Special reserved path for system pages, we do not treat them as webnode nor can have the webnode page with such path.
	 */
	public static String[] IMMUTABLE_PATHS_REGEX = new String[]{"/site$", "/ish/internal/autocomplete.sub$",
			"/ui/textileform.send$", "/ui/timezoneholder\\.$"};

	/**
	 * Path of the removing from cookies request
	 */
	private static final String REMOVE_FROM_COOKIES_PATH = "/removeFromCookies";

	/**
	 * Path of the adding to cookies request
	 */
	private static final String ADD_TO_COOKIES_PATH = "/addToCookies";

	/**
	 * Store cookie value for student unique code for 20 minutes
	 */
	private static final int STUDENT_EXPIRE_TIME = 1200;

	private static final String CHECKOUT_STATUS = "paymentStatus=";
	private static final String CHECKOUT_SESSION_ID = "sessionId=";


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
	ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITutorService tutorService;

	@Inject
	private IWebUrlAliasService webUrlAliasService;

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {

		final String path = request.getPath().toLowerCase();

		if (path.startsWith(ISHHealthCheckServlet.ISH_HEALTH_CHECK_PATTERN.toLowerCase())) return null;

		if (isCheckoutRedirect()) {
			return new PageRenderRequestParameters(PageIdentifier.CheckoutRedirect.getPageName(), new EmptyEventContext(), false);
		}

		String studentUniqCode = request.getParameter(Contact.STUDENT_PROPERTY);

		if (StringUtils.trimToNull(studentUniqCode) != null) {
			request.setAttribute(Contact.STUDENT_PROPERTY, studentUniqCode);
			cookiesService.writeCookieValue(Contact.STUDENT_PROPERTY, studentUniqCode, STUDENT_EXPIRE_TIME);
		}

		studentUniqCode = cookiesService.getCookieValue(Contact.STUDENT_PROPERTY);
		if (StringUtils.trimToNull(studentUniqCode) != null) {
			request.setAttribute(Contact.STUDENT_PROPERTY, studentUniqCode);
		}

		logger.info("Rewrite InBound: path is: {}", path);

		PageIdentifier pageIdentifier = PageIdentifier.getPageIdentifierByPath(path);

		/**
		 * ISHHealthCheck can be used without college keyCode.
		 */
		if (webSiteService.getCurrentWebSite() == null) {
			requestGlobals.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new PageRenderRequestParameters(PageIdentifier.SiteNotFound.getPageName(), new EmptyEventContext(), false);
		}

		PageRenderRequestParameters specialRqParams = getSpecialRedirect(path);
		if (specialRqParams != null) {
		    return specialRqParams;
        }

		/**
		 * ISHHealthCheck can be used without college keyCode.
		 */
		if (needRedirect(request)) {
			return new PageRenderRequestParameters("ui/internal/redirect301", new EmptyEventContext(), false);
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
				final boolean isCMSCoursesSearch = path.startsWith(CMS_PATH + COURSES_PATH);

				String tagsPath = path
						.replaceFirst(isCMSCoursesSearch ? CMS_PATH + COURSES_PATH : COURSES_PATH, StringUtils.EMPTY);

				logger.debug("tagsPath: {}", tagsPath);

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
						request.setAttribute(ATTR_coursesTag, tag);
					}
				}
				break;
			case Product:
				Product product = null;
				String productSKU = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
				if (productSKU != null) {
					product = voucherService.getProductBySKU(productSKU);
				}
				if (product != null) {
					request.setAttribute(Product.class.getSimpleName(), product);
				} else {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case Course:
				Course course = GetCourseByPath
						.valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), path)
						.get();
				if (course != null) {
					request.setAttribute(Course.class.getSimpleName(), course);
				} else {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case CourseClass:
				CourseClass courseClass = GetCourseClassByPath
						.valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), path)
						.get();
				if (courseClass != null) {
					request.setAttribute(CourseClass.class.getSimpleName(), courseClass);
				} else {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case Page:
				String nodeNumber = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
				WebNode webNode = null;
				if (isNumeric(nodeNumber)) {
					request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
					webNode = webNodeService.getCurrentNode();
				}
				if (webNode == null) {
					request.setAttribute(IWebNodeService.PAGE_PATH_PARAMETER, path);
					webNode = webNodeService.getNodeForNodePath(path);
				}
				if (webNode == null) {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case Sites:
				break;
			case Site:
			case KioskSite:
				Site site = new GetSiteByAngelId().path(path).sitesService(sitesService).get();
				if (site != null) {
					request.setAttribute(Site.class.getSimpleName(), site);
				} else {
					return new PageRenderRequestParameters(PageIdentifier.PageNotFound.getPageName(), new EmptyEventContext(), false);
				}
				break;
			case Room:
			case KioskRoom:
				Room room = null;
				String roomId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
				if (isNumeric(roomId)) {
					room = roomService.getRoom(Room.ANGEL_ID_PROPERTY, Long.valueOf(roomId));
				}
				if (room != null) {
					request.setAttribute(Room.class.getSimpleName(), room);
					request.setAttribute(Site.class.getSimpleName(), room.getSite());
				} else {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case Tutor:
				String tutorId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
				if (tutorId != null && tutorId.length() > 0 && tutorId.matches("\\d+")) {
					Tutor tutor = tutorService.findByAngelId(Long.valueOf(tutorId));
					if (tutor == null || !GetIsActiveTutor.valueOf(tutor).get()) {
						pageIdentifier = PageIdentifier.PageNotFound;
					}
					request.setAttribute(TUTOR_ATTRIBUTE, tutor);
				} else {
					pageIdentifier = PageIdentifier.PageNotFound;
				}
				break;
			case Sitemap:
			case Robots:
			case AdvancedKeyword:
			case AdvancedSuburbs:
			case Shortlist:
			case AddDiscount:
			case Promotions:
			case Timeline:
			case CoursesSitesMap:
			case Cvv:
				break;
		}

		if (pageIdentifier != PageIdentifier.PageNotFound) {
			return new PageRenderRequestParameters(pageIdentifier.getPageName(), new EmptyEventContext(), false);
		}

		if (ADD_TO_COOKIES_PATH.equalsIgnoreCase(path) || REMOVE_FROM_COOKIES_PATH.equalsIgnoreCase(path)) {
			boolean isAddAction = ADD_TO_COOKIES_PATH.equalsIgnoreCase(path);
			String key = request.getParameter(KEY_PARAMETER);
			String value = isAddAction ? request.getParameter(ADD_ITEM_ID_PARAMETER) : request.getParameter(REMOVE_ITEM_ID_PARAMETER);
			if (key != null && ArrayUtils.contains(COOKIE_KEYS, key) && value != null) {
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

		if (Arrays.stream(IMMUTABLE_PATHS_REGEX).anyMatch(imm -> {
            Pattern p = Pattern.compile(imm);
            Matcher m = p.matcher(path);
            return m.matches();
        })) return null;

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

	private boolean needRedirect(Request request) {
		String path = requestGlobals.getHTTPServletRequest().getPathInfo();
		String query = requestGlobals.getHTTPServletRequest().getQueryString();

		WebUrlAlias redirect = webUrlAliasService.getAliasByPath(URLPath.valueOf(path).getEncodedPath());
		if (redirect != null && redirect.getRedirectTo() != null) {
			request.setAttribute(REQUEST_ATTR_redirectTo, redirect.getRedirectTo());
			request.setAttribute(REQUEST_ATTR_redirectToParams, query);
			return true;
		}
		return false;
	}

	private PageRenderRequestParameters getSpecialRedirect(String path) {
        PageRenderRequestParameters rqParams = null;

        WebUrlAlias specialRedirectAlias = SpecialWebPageMatcher
                .valueOf(cayenneService.newContext(), QueryCacheStrategy.LOCAL_CACHE, webSiteService.getCurrentWebSite(), path)
                .get();
        if (specialRedirectAlias != null && specialRedirectAlias.getSpecialPage() != null && specialRedirectAlias.getMatchType() != null) {
            rqParams = new PageRenderRequestParameters(specialRedirectAlias.getSpecialPage().getTemplatePath(), new EmptyEventContext(), false);
        }
        return  rqParams;
    }

    private boolean isCheckoutRedirect() {
		if (requestGlobals.getHTTPServletRequest() == null) {
			return false;
		}
		String query = requestGlobals.getHTTPServletRequest().getQueryString();
		return query != null && query.contains(CHECKOUT_STATUS) && query.contains(CHECKOUT_SESSION_ID);

	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		logger.info("Rewrite OutBound: path is: {}", defaultLink.getBasePath());

		return defaultLink;
	}

	public static class GetSiteByAngelId {

		private String path;
		private ISitesService sitesService;

		public GetSiteByAngelId path(String path) {
			this.path = path;
			return this;
		}

		public GetSiteByAngelId sitesService(ISitesService sitesService) {
			this.sitesService = sitesService;
			return this;
		}

		public Site get() {
			Site site = null;
			String siteId = path.substring(path.lastIndexOf(LEFT_SLASH_CHARACTER) + 1);
			if (isNumeric(siteId)) {
				site = sitesService.getSite(Site.ANGEL_ID_PROPERTY, siteId);
			}
			return site;
		}

	}
}
