package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class BlockDisplay extends ISHCommon {

	@Parameter
	private WebNodeType webNodeType;

	@Parameter
	private String regionKey;

	@Property
	private List<WebContent> regions;
	
	@Inject
	private IParsedContentVisitor visitor;

	@Property
	private WebContent region;

	@SetupRender
	public boolean beforeRender() {
		if (webNodeType == null) {
			return false;
		}
		this.regions = webNodeType.getContentForRegionKey(regionKey);
		if (regions == null) {
			return false;
		}
		return true;
	}

	public String getRegionContent() {
		return visitor.visitWebContent(region);
	}
}
