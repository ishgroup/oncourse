package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;

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
	 * Gets blocks for location key, sorted by weight property.
	 * 
	 * @param location key
	 * @return web blocks.
	 */
	SortedSet<WebContent> getBlocksForRegionKey(RegionKey regionKey);
	
	/**
	 * Gets web content visibility for region key.
	 * @param regionKey
	 * @return web content visibility
	 */
	SortedSet<WebContentVisibility> getBlockVisibilityForRegionKey(RegionKey regionKey);
	
	List<WebContent> getBlocks();
}
