package ish.oncourse.services.resource;


public interface IResourceService {

	/**
	 * Extracts template for particular layout key from the database.
	 * 
	 * @param layoutKey - layout key of template
	 * @param fileName - file name of template
	 */
	org.apache.tapestry5.ioc.Resource getDbTemplateResource(String layoutKey, String fileName);

	/**
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a web resource for the current web host name.
	 */
	Resource getWebResource(String fileName);
	
}
