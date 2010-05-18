package ish.oncourse.ui.components;

import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.assetgroup.AssetType;
import ish.oncourse.services.assetgroup.IAssetGroupService;

public class JSAssetGroup {
	@Inject
	private IAssetGroupService assetGroupService;

	@Parameter(required = true)
	private String group;
	
	@Property
	private String url;
	
	public Collection<String> getUrls() {
		return assetGroupService.getAssetGroupUrls(AssetType.js, group);
	}
}
