package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;

import java.util.List;
import java.util.SortedSet;

public interface IWebContentService {

	/**
	 * 
	 * @param searchProperty
	 * @param value
	 * @return
	 */

	WebContent getWebContent(String searchProperty, Object value);

    /**
     * 
     * @param willowId
     * @return
     */
	WebContent findById(Long willowId);

	/**
	 * Gets blocks for location key for given web node type, sorted by weight property.
	 * 
	 * @param webNodeType
	 * @param location key
	 * @return web blocks.
	 */
	SortedSet<WebContent> getBlocksForRegionKey(WebNodeType webNodeType, RegionKey regionKey);
	
	/**
	 * Gets web content visibility for region key and given webNodeType.
	 * 
	 * @param webNodeType
	 * @param regionKey
	 * @return web content visibility
	 */
	List<WebContentVisibility> getBlockVisibilityForRegionKey(WebNodeType webNodeType, RegionKey regionKey);
	
	/**
	 * Set the correct weight for visibility to match the required position
	 * @param webNodeType
	 * @param regionKey
	 * @param webContentVisibility
	 * @param position
	 */
	void putWebContentVisibilityToPosition(WebNodeType webNodeType, RegionKey regionKey, WebContentVisibility webContentVisibility, int position);
	
	List<WebContent> getBlocks();
}
