package ish.oncourse.services.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Represents private file which isn't accessible by Apache
 * web server thus it doesn't have private URL.
 * It's typically used for config files (js.conf, css.conf) 
 */

public class FileResource implements PrivateResource {
	
	protected String fileName;
	protected String folder;
	private File file;

	public FileResource(String folder, String fileName) {
		this.fileName = (fileName.startsWith("/")) ? fileName.substring(1)
				: fileName;
		this.folder = folder;
		this.file = new File(this.folder, this.fileName);
	}

	public File getFile() {
		return this.file;
	}

	public URL getPrivateUrl() {
		try {
			return getFile().toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exists() {
		return getFile().exists();
	}
}
