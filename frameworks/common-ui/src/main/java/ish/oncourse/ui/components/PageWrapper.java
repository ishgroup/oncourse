package ish.oncourse.ui.components;

import ish.oncourse.services.security.IAuthenticationService;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * A page wrapper component.
 */
public class PageWrapper {

	private static final String MSIE = "MSIE";
	private static final String MAIN_PAGE_REAL_NAME = "Index";

	@Inject
	private ComponentResources componentResources;

	@Inject
	private IAuthenticationService authenticationService;
	
	@Parameter
	@Property
	private String bodyId;

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}

	@Inject
	private Request request;

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
