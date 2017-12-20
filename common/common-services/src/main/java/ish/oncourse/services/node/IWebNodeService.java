package ish.oncourse.services.node;

import ish.oncourse.model.*;
import ish.oncourse.services.IBaseService;
import ish.oncourse.services.cache.RequestCached;

import java.util.Date;
import java.util.List;

public interface IWebNodeService extends IBaseService<WebNode> {

	public static final String CURRENT_WEB_NODE_LAYOUT = "CURRENT_WEB_NODE_LAYOUT";

	static final String NODE_NUMBER_PARAMETER = "n";
	static final String PAGE_PATH_PARAMETER = "p";
	 
	/**
	 * Returns next node number which is unique within the site.
	 * @return next node number.
	 */
	 Integer getNextNodeNumber();
	
	/**
	 * Returns all web nodes for the current site or current college.
	 */
    @RequestCached
	List<WebNode> getNodes();

	/**
	 * Returns home page for current site.
	 * 
	 * @return
	 */
    @RequestCached
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
    @RequestCached
	WebNode getCurrentNode();

    @RequestCached
	WebSiteLayout getLayout();
	
	/**
	 * Return random webNode for the current website.
	 * @return
	 */
	WebNode getRandomNode();

	/**
	 * The latest date when site page was modified.
	 */
    @RequestCached
	Date getLatestModifiedDate();
	
	/**
	 * Creates and returns the new node.
	 * @return
	 */
	WebNode createNewNode();

    /**
     * Creates new WebNode by these parameters.
     */
   WebNode createNewNodeBy(WebSiteVersion webSiteVersion,
                           WebNodeType webNodeType,
                           String nodeName,
                           String content,
                           Integer nodeNumber);

    /**
     * Returns default alias for the webNode
     */
    WebUrlAlias getDefaultWebURLAlias(WebNode webNode);

    /**
     * Build url path for the webNode.
     */
    public String getPath(WebNode webNode);

	/**
	 * Returns {@link WebNode} with specified name, null if there is no node with such name.
	 */
	WebNode getNodeForName(String nodeName);
}
