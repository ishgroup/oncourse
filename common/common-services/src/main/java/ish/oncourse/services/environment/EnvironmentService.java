package ish.oncourse.services.environment;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationGlobals;

import java.io.InputStream;
import java.util.jar.Manifest;

public class EnvironmentService implements IEnvironmentService {
	private ApplicationGlobals applicationGlobals;

	@Inject
	public EnvironmentService(ApplicationGlobals applicationGlobals) {
		this.applicationGlobals = applicationGlobals;
	}

	private String ciVersion;

	public String getApplicationName() {
		return "onCourse";
	}

	public String getBuildServerID() {
		return "";
	}

	public String getScmVersion() {
		return "";
	}

	public boolean isTransientEnvironment() {
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
