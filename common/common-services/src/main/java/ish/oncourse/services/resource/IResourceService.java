package ish.oncourse.services.resource;


import ish.oncourse.model.WebSiteLayout;

public interface IResourceService {

	/**
	 * Extracts template for particular layout key from the database.
	 * 
	 * @param layout - layout of template
	 * @param fileName - file name of template
	 */
	org.apache.tapestry5.ioc.Resource getDbTemplateResource(WebSiteLayout layout, String fileName);

	/**
	 * @param fileName
	 *            resource filename
	 * 
	 * @return a web resource for the current web host name.
	 */
	Resource getWebResource(String fileName);
	
}
