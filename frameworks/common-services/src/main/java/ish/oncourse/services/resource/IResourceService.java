package ish.oncourse.services.resource;

public interface IResourceService {


	/**
	 * Template getter for a particular templateKey.
	 *
	 * <p>Note that if no template is found for the templateKey the defaults
	 *
	 * @param templateKey template key
	 * @param fileName resource filename
	 *
	 * @return a Tapestry 5 template override resource for the current web host
	 * name.
	 */
	PrivateResource getTemplateResource(String templateKey, String fileName);

	/**
	 * @param fileName config file name
	 *
	 * @return a config resource for the current web host name.
	 */
	PrivateResource getConfigResource(String fileName);

	/**
	 * @param framework 
	 * @param fileName resource filename
	 *
	 * @return a web resource for the current web host name.
	 */
	Resource getWebResource(String framework, String fileName);

}
