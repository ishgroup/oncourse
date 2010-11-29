package ish.oncourse.services.content;

import java.util.List;
import java.util.SortedSet;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;

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
	 * Returns all web blocks for the current site or current college.
	 */
	List<WebContent> getBlocks();

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
}
