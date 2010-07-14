package ish.oncourse.ui.components;

import org.apache.cayenne.DataObjectUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.site.IWebSiteService;
import java.util.List;


// TODO: MSW 2010/06/03 The WebBlocks are defined within the Textile content
// of the WebNode.
// The WebNode will identify the injected WebBlock by its name. There should only
// be one WebBlock per WebSite with that name.
public class WebRegionDisplay {

	@Parameter
	@Property
	private String regionKey;

	@Property
	private WebBlock block;

	@Inject
	private IWebSiteService webSiteService;

	public List<WebBlock> getBlocks() {
		return webSiteService.getWebBlocksForRegion(regionKey);
	}

	public String getRegionId() {
		return "region_" + regionKey;
	}

	public String getBlockId() {
		return "block_" + DataObjectUtils.intPKForObject(block);
	}
}
