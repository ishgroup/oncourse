package ish.oncourse.services.environment;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.host.IWebHostNameService;

public class EnvironmentService implements IEnvironmentService {

	@Inject
	private IWebHostNameService webHostNameService;

	public String getApplicationName() {
		// TODO hardcoded
		return "onCourse";
	}

	public String getBuildServerID() {
		// TODO hardcoded
		// ERXProperties.stringForKey( "hudson.version" );
		return "";
	}

	public String getScmVersion() {
		// TODO hardcoded
		// ERXProperties.stringForKey( "scm.version" )
		return "";
	}

	public boolean isTransientEnvironment() {
		// TODO: pending webHostNameService.getCurrentWebHostName()
		// implementation
		// WebHostName host = webHostNameService.getCurrentWebHostName();
		// return host.getName().contains("local.oncourse")
		// || host.getName().contains("test.oncourse")
		// || host.getName().contains("staging.oncourse");
		return true;
	}
}
