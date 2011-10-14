package ish.oncourse.services.resource;

import java.io.File;
import java.util.List;

public interface IResourceService {

	/**
	 * Template getter for a particular layoutKey.
	 * 
	 * <p>
	 * Note that if no template is found for the layoutKey the defaults
	 * 
	 * @param layoutKey of WebNodeType
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a Tapestry 5 template override resource for the current web host
	 *         name.
	 */
	PrivateResource getTemplateResource(String layoutKey, String fileName);

	/**
	 * @param fileName
	 *            config file name
	 * 
	 * @return resources for the current web host name and default config.
	 */
	List<PrivateResource> getConfigResources(String fileName);

	/**
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a web resource for the current web host name.
	 */
	Resource getWebResource(String fileName);
	
	/**
	 * Returns default resource root folder
	 * @return resource folders for current website.
	 */
	File getCustomComponentRoot();
}
