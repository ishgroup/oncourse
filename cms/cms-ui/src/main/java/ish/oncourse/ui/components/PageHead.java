package ish.oncourse.ui.components;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.college.ICollegeService;
import ish.oncourse.services.environment.IEnvironmentService;

public class PageHead {

	@Inject
	private IEnvironmentService environmentService;

	@Inject
	private ICollegeService collegeService;

	public String getTitle() {
		return collegeService.getCurrentCollege().getName();
	}

	public String getMetaGeneratorContent() {
		StringBuilder buff = new StringBuilder(environmentService
				.getApplicationName());

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
		return buff.toString();
	}
}
