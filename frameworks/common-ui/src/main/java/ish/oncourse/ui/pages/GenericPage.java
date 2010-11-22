package ish.oncourse.ui.pages;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.dynamic.ContentDelegate;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public abstract class GenericPage {

	private static final String WELCOME_TEMPLATE_ID = "welcome";

	@Inject
	private ComponentResources resources;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private Block regionBlock;

	@Property
	@Persist
	private WebContent region;

	@Property
	@Persist
	private WebNode node;

	private ContentDelegate _dynamicPart = new ContentDelegate(2) {
		public ComponentResources getComponentResources() {
			return resources;
		}

		public Block getBlock(String regionKey) {
			Block paramBlock = resources.getBlockParameter(regionKey);

			if (paramBlock != null) {
				return paramBlock;
			}

			List<WebContentVisibility> list = ExpressionFactory.matchExp(
					WebContentVisibility.REGION_KEY_PROPERTY, RegionKey.valueOf(regionKey.toUpperCase()))
					.filterObjects(node.getWebContentVisibility());

			if (list.size() > 0) {
				region = list.get(0).getWebContent();
				return regionBlock;
			}

			return null;
		}
	};

	@SetupRender
	public void beforeRender() {
		setCurrentNode(webNodeService.getCurrentNode());
	}

	public String getTemplateId() {
		return (webNodeService.getHomePage().getId() == this.node.getId()) ? WELCOME_TEMPLATE_ID
				: "";
	}

	public String getRegionContent() {
		return webContentService.getParsedContent(this.region);
	}

	public WebNode getCurrentNode() {
		return this.node;
	}

	public void setCurrentNode(WebNode node) {
		this.node = node;
	}

	public void setCurrentRegion(WebContent region) {
		this.region = region;
	}

	public WebContent getCurrentRegion() {
		return this.region;
	}

	public String getBodyId() {
		return (isHomePage()) ? "Main" : ("page" + this.node.getId());
	}

	public String getBodyClass() {
		return (isHomePage()) ? "main-page" : "internal-page";
	}

	private boolean isHomePage() {
		return getCurrentNode().getId() != null
				&& getCurrentNode().getId().equals(
						webNodeService.getHomePage().getId());
	}

	public ContentDelegate getDelegate() {
		return _dynamicPart;
	}
}
