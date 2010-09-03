package ish.oncourse.services.node;

import java.util.List;

import ish.oncourse.model.WebNode;

public interface IWebNodeService {

	String NODE = "node";
	/**
	 * Returns all web nodes for the current site or current college.
	 */
	List<WebNode> getNodes();
	
	/**
	 * Returns home page for current site.
	 * @return
	 */
	WebNode getHomePage();
	
	/**
	 * Returns the current page active page.
	 *
	 * Note that this method relies on the injected
	 * {@link org.apache.tapestry5.services.Request} object.
	 * 
	 * @return the current page
	 */
	WebNode getCurrentPage();

	WebNode getNode(String searchProperty, Object value);

}
