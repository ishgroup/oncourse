package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.IBaseService;

import java.util.Date;
import java.util.List;

public interface IWebNodeService {

	 String NODE = "node";
	 String NODE_NUMBER_PARAMETER = "n";
	 String PAGE_PATH_PARAMETER = "p";
	 String WEB_NODE_PAGE_TYPE_KEY = "Page";
	 String WELCOME_TEMPLATE_ID = "welcome";

	/**
	 * Returns next node number which is unique within the site.
	 * @return next node number.
	 */
	 Integer getNextNodeNumber();
	
	/**
	 * @see IBaseService#findById(Long)
	 */
	WebNode findById(Long willowId);
	
	/**
	 * Returns all web nodes for the current site or current college.
	 */
	List<WebNode> getNodes();

	/**
	 * Returns home page for current site.
	 * 
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
	 * Find a web node for the node path.
	 * 
	 * @param nodePath
	 * @return corresponding web node or null if not found
	 */
	WebNode getNodeForNodePath(String nodePath);

	/**
	 * Returns the current page active page.
	 * 
	 * Note that this method relies on the injected
	 * {@link org.apache.tapestry5.services.Request} object.
	 * 
	 * @return the current page
	 */
	WebNode getCurrentNode();

	/**
	 * Search by property and value.
	 * 
	 * @param searchProperty
	 * @param value
	 * @return
	 */
	WebNode getNode(String searchProperty, Object value);

	/**
	 * The latest date when site page was modified.
	 */
	Date getLatestModifiedDate();

	/**
	 * If node exists for path.
	 */
	boolean isNodeExist(String path);
}
