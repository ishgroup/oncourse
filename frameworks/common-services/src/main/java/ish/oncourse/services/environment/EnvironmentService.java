package ish.oncourse.services.environment;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.site.IWebSiteService;


public class EnvironmentService implements IEnvironmentService {

	@Inject
	private IWebSiteService siteService;

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
	 * <p>Attempts to read the CI Build Number form the Manifest file. Looks for
	 * the 'Implementation-Version' environment variable.</p>
	 *
	 * @return CI version number as String
	 */
	public String getCiVersion() {
		String ciVersion = "n/a";
		Manifest manifest = null;

		try {
			InputStream is = new BufferedInputStream(new FileInputStream("/META-INF/MANIFEST.MF"));
			try {
				manifest = new Manifest(is);
				ciVersion = manifest.getMainAttributes().getValue("Implementation-Version");
			} finally {
				is.close();
			}
		} catch (IOException e) {
			
		}

		return ciVersion;
	}

}
