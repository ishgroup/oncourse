package ish.oncourse.ui.components;

import java.util.Collection;

import org.apache.cayenne.DataObjectUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.site.IWebSiteService;

public class WebRegionDisplay {

	@Property
	@Parameter
	private String regionKey;

	@Property
	private WebBlock block;

	@Inject
	private IWebSiteService webSiteService;

	public Collection<WebBlock> getBlocks() {

		return webSiteService.getActiveBlocks(regionKey);
	}

	public String getRegionId() {
		return "region_" + regionKey;
	}

	public String getBlockId() {
		return "block" + DataObjectUtils.intPKForObject(block);
	}
}
