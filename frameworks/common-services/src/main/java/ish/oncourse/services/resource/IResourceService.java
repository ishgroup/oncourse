package ish.oncourse.services.resource;

import java.util.List;

public interface IResourceService {

	/**
	 * Template getter for a particular templateKey.
	 * 
	 * <p>
	 * Note that if no template is found for the templateKey the defaults
	 * 
	 * @param templateKey
	 *            template key
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a Tapestry 5 template override resource for the current web host
	 *         name.
	 */
	PrivateResource getTemplateResource(String templateKey, String fileName);

	/**
	 * @param fileName
	 *            config file name
	 * 
	 * @return resources for the current web host name and default config.
	 */
	List<PrivateResource> getConfigResources(String fileName);

	/**
	 * @param framework
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a web resource for the current web host name.
	 */
	Resource getWebResource(String fileName);

}
