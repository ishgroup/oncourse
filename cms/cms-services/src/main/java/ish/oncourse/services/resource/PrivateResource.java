package ish.oncourse.services.resource;

import java.io.File;
import java.net.URL;

/**
 * Represents a resource not visible via the web server.
 */
public interface PrivateResource {

	/**
	 * Returns writable resource URL, usually a file URL. This URL for
	 * reading/writing resources by the application and should not be referenced
	 * on the web pages.
	 * 
	 * @return the URL
	 *
	 * @throws IllegalStateException
	 *             for missing files.
	 */
	URL getPrivateUrl() throws IllegalStateException;

	/**
	 * Returns a file for this resource. Would throw IllegalStateException for
	 * stream resources, such as classpath resources.
	 *
	 * @return a file
	 *
	 * @throws IllegalStateException
	 *             for stream resources, such as classpath resources, as well as
	 *             for missing files.
	 */
	File getFile() throws IllegalStateException;
}
