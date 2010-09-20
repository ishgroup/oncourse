package ish.oncourse.services.node;

import java.util.Date;
import java.util.List;

import ish.oncourse.model.WebNode;

public interface IWebNodeService {

	public static final String NODE = "node";
	public static final String NODE_NUMBER_PARAMETER = "n";
	public static final String PAGE_PATH_PARAMETER = "p";
	public static final String WEB_NODE_PAGE_TYPE_KEY = "Page";


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
	 * Find a web node for the corresponding node number.
	 * 
	 * @param nodeNumber
	 * @return corresponding web node or null if not found
	 */
	WebNode getNodeForNodeNumber(Integer nodeNumber);

	/**
	 * Find a web node for the corresponding node name.
	 *
	 * @param nodeName
	 * @return corresponding web node or null if not found
	 */
	WebNode getNodeForNodeName(String nodeName);

	/**
	 * Returns the current page active page.
	 *
	 * Note that this method relies on the injected
	 * {@link org.apache.tapestry5.services.Request} object.
	 *
	 * @return the current page
	 */
	WebNode getCurrentPage();

	/**
	 *
	 * @param searchProperty
	 * @param value
	 * @return
	 */
	WebNode getNode(String searchProperty, Object value);

	Date getLatestModifiedDate();
}
