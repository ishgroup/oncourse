package ish.oncourse.services.environment;

import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationGlobals;

import java.io.InputStream;
import java.util.jar.Manifest;

public class EnvironmentService implements IEnvironmentService {

	@SuppressWarnings("all")
	@Inject
	private IWebSiteService siteService;

	@Inject
	private ApplicationGlobals applicationGlobals;
	
	private String ciVersion;

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

	/**
	 * Getter for the CI Build Number.
	 * 
	 * <p>
	 * Attempts to read the CI Build Number form the Manifest file. Looks for
	 * the 'Implementation-Version' environment variable.
	 * </p>
	 * 
	 * @return CI version number as String
	 */
	public String getCiVersion() {
		if (ciVersion == null) {
			synchronized (this) {
				ciVersion = "";
				Manifest manifest = null;

				try {
					InputStream is = applicationGlobals.getServletContext().getResourceAsStream(
									"/META-INF/MANIFEST.MF");
					if (is != null) {
						try {
							manifest = new Manifest(is);
							ciVersion = manifest.getMainAttributes().getValue(
									"Implementation-Version");
						} finally {
							is.close();
						}
					}
				} catch (Exception e) {

				}
			}
		}
		return ciVersion;
	}

}
