package ish.oncourse.services.node;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.cache.RequestCached;

import java.util.List;

public interface IWebNodeTypeService {
	/**
	 * Returns default webNodeType for the current site.
	 * 
	 * @return default webNodeType
	 */
    @RequestCached
	WebNodeType getDefaultWebNodeType();
	
	/**
	 * Returns all available web node types for the current site. Used in CMS on PageOptions screen.
	 * 
	 * @return available web node types.
	 */
    @RequestCached
	List<WebNodeType> getWebNodeTypes();
	
	/**
	 * Gets webNode type by id. Used in CMS on PageTypes screen.
	 * @param willowId
	 * @return
	 */
	WebNodeType findById(Long willowId);
}
