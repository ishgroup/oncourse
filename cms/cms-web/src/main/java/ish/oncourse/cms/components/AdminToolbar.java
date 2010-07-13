package ish.oncourse.cms.components;

import ish.oncourse.cms.pages.Login;
import ish.oncourse.cms.services.security.IAuthenticationService;
import ish.oncourse.cms.services.security.annotations.Protected;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.PersistentSelectModel;

import java.io.IOException;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

@Protected
@Import(library="AdminToolbar.js")
public class AdminToolbar {
	
	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private Request request;

	@Property
	private WebSite selectedSite;

	@InjectPage
	private Login loginPage;

	public SelectModel getSites() {
		return new PersistentSelectModel(webSiteService.getAvailableSites(),
				WebSite.CODE_PROPERTY, WebSite.NAME_PROPERTY);
	}

	public String getAdminDivClass() {
		return environmentService.isTransientEnvironment() ? "admin blownaway"
				: "admin";
	}

	public Boolean getIsLoggedIn() {
		return authenticationService.getUser() != null;
	}

	public String getUserFirstName() {
		return authenticationService.getUser().getFirstName();
	}

	public boolean isMultipleSites() {
		return webSiteService.getAvailableSites().size() > 1;
	}

	public Object onActionFromLogout() throws IOException {

		Session session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		return loginPage;
	}
}
