package ish.oncourse.services.resource;

import ish.oncourse.services.resource.Resource;
import java.io.File;
import java.net.URL;

public class MockResource implements Resource {

	public URL getPrivateUrl() {
		throw new UnsupportedOperationException();
	}

	public String getPublicUrl() {
		throw new UnsupportedOperationException();
	}

	public File getFile() {
		throw new UnsupportedOperationException();
	}

}
