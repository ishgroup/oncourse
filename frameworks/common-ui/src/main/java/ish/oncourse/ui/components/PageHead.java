package ish.oncourse.ui.components;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.security.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.environment.IEnvironmentService;

public class PageHead {

	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private IAuthenticationService authenticationService;
	
	@Parameter
	private String title;
	
	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}

	public String getTitle() {
		String collegeName = siteService.getCurrentCollege().getName();
		
		if (title != null) {
			return title + " " + collegeName;
		}
		
		return collegeName;
	}

	public String getMetaGeneratorContent() {
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
