package ish.oncourse.services.resource;

/**
 * Represents a web resource.
 */
public interface Resource extends PrivateResource {

	/**
	 * Returns a public resource URL, that can be a relative URL.
	 */
	String getPublicUrl();
}
