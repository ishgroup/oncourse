package ish.oncourse.services.content;

import java.util.List;

import ish.oncourse.model.WebContent;

public interface IWebContentService {
	
	/**
	 * 
	 * @param webContent
	 * @return
	 */
	
	String getParsedContent(WebContent webContent);
	
	/**
	 * 
	 * @param searchProperty
	 * @param value
	 * @return
	 */
	
	WebContent getWebContent(String searchProperty, Object value);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	List<WebContent> loadByIds(Object... ids);
	
	/**
	 * Returns all web blocks for the current site or current college.
	 */
	List<WebContent> getBlocks();
	
	/**
	 * Creates new web content.
	 * @return
	 */
	WebContent newWebContent();
}
