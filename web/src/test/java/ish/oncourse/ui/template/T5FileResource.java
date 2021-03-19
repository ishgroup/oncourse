package ish.oncourse.ui.template;

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.AbstractResource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class T5FileResource extends AbstractResource {

	private File file;

	public T5FileResource(File file) {
		super(file.getAbsolutePath());
		this.file = file;
	}

	@Override
	protected Resource newResource(String path) {
		File child = new File(file, path);
		return new T5FileResource(child);
	}

	public URL toURL() {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error getting file URL", e);
		}
	}

	@Override
	public String toString() {
		return String.format("context:%s", getPath());
	}
}
