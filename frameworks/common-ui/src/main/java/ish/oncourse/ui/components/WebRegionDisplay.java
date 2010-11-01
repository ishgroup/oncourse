package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;


// TODO: MSW 2010/06/03 The WebBlocks are defined within the Textile content
// of the WebNode.
// The WebNode will identify the injected WebContent by its name. There should only
// be one WebContent per WebSite with that name.
public class WebRegionDisplay {

	@Parameter
	@Property
	private String regionKey;

	@Property
	private WebContent block;

	@Inject
	private IWebSiteService webSiteService;

	public List<WebContent> getBlocks() {
		return webSiteService.getWebBlocksForRegion(regionKey);
	}

	public String getRegionId() {
		return "region_" + regionKey;
	}

	public String getBlockId() {
		return "block_" + block.getId();
	}
}
