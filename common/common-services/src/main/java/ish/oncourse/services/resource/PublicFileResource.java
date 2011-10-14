package ish.oncourse.services.resource;

/**
 * Public file accessible by Apache. Typically static web resource
 * css or javascript files.
 */

public class PublicFileResource extends FileResource implements Resource {
	public PublicFileResource(String folder, String fileName) {
		super(folder, fileName);
	}

	public String getPublicUrl() {
		return "/" + folder + "/" + fileName;
	}
}
