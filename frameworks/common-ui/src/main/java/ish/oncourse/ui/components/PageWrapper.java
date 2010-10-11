package ish.oncourse.ui.components;

import ish.oncourse.services.security.IAuthenticationService;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * A page wrapper component.
 */
public class PageWrapper {

	private static final String MSIE = "MSIE";
	private static final String COURSES_PAGE_NAME = "ListPage";
	private static final String COURSES_PAGE_REAL_NAME = "ui/Courses";
	private static final String SITES_PAGE_NAME = "SiteList";
	private static final String SITES_PAGE_REAL_NAME = "ui/Sites";
	private static final String WEB_NODE_COMPONENT_NAME = "webNodeTemplate";
	private static final String WEB_NODE_PAGE_NAME = "ui/Page";
	private static final String MAIN_PAGE_NAME = "Main";
	private static final String MAIN_PAGE_REAL_NAME = "Index";
	private static final String DETAILS_PAGE_NAME = "DetailsPage";
	private static final String COURSE_PAGE_REAL_NAME = "ui/CourseDetails";
	private static final String COURSE_CLASS_PAGE_REAL_NAME = "ui/CourseClassDetails";
	private static final String SITE_PAGE_REAL_NAME = "ui/SiteDetails";
	private static final String ROOM_PAGE_REAL_NAME = "ui/RoomDetails";
	private static final String TUTOR_PAGE_REAL_NAME = "ui/TutorDetails";

	@Inject
	private ComponentResources componentResources;

	@Inject
	private IAuthenticationService authenticationService;

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}

	@Inject
	private Request request;

	public String getBodyId() {

		String pageName = componentResources.getPageName();

		if (MAIN_PAGE_REAL_NAME.equals(pageName)) {
			return MAIN_PAGE_NAME;
		} else if (WEB_NODE_PAGE_NAME.equals(pageName)) {
			Long nodeNumber = ((WebNodeTemplate) componentResources.getPage()
					.getComponentResources()
					.getEmbeddedComponent(WEB_NODE_COMPONENT_NAME)).getNode()
					.getId();
			return pageName.toLowerCase().replaceFirst("ui/", "") + nodeNumber;
		} else if (COURSES_PAGE_REAL_NAME.equals(pageName)) {
			return COURSES_PAGE_NAME;
		} else if (COURSE_PAGE_REAL_NAME.equals(pageName)
				|| COURSE_CLASS_PAGE_REAL_NAME.equals(pageName)
				|| SITE_PAGE_REAL_NAME.equals(pageName)
				|| TUTOR_PAGE_REAL_NAME.equals(pageName)
				|| ROOM_PAGE_REAL_NAME.equals(pageName)) {
			return DETAILS_PAGE_NAME;
		} else if (SITES_PAGE_REAL_NAME.equals(pageName)) {
			return SITES_PAGE_NAME;
		}

		return pageName;

	}

	public String getBodyClass() {
		String pageName = componentResources.getPageName();

		String bodyClass = MAIN_PAGE_REAL_NAME.equals(pageName) ? "main-page"
				: "internal-page";

		String userAgent = request.getHeader("User-Agent");
		if (userAgent.indexOf(MSIE) > -1) {
			int versionPosition = userAgent.indexOf(MSIE) + MSIE.length() + 1;
			Integer versionNumber = Integer.parseInt(userAgent.substring(
					versionPosition, versionPosition + 1));
			switch (versionNumber) {
			case 7:
				return bodyClass + " ie7";
			case 8:
				return bodyClass + " ie8";
			case 9:
				return bodyClass + " ie9";
			default:
				if (versionNumber < 7) {
					return bodyClass + " ie6";
				}
				if (versionNumber > 9) {
					return bodyClass;
				}
			}

		}

		return bodyClass;
	}
}
