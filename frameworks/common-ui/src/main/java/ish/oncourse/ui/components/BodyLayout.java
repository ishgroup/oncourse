package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.dynamic.ContentDelegateComposite;
import ish.oncourse.ui.dynamic.ContentDelegate;

import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

@SupportsInformalParameters
public class BodyLayout {

	@Inject
	private ComponentResources resources;

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private Block regionBlock;

	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	private WebNodeType webNodeType;

	@Property
	private List<WebContent> currentRegions;

	@Property
	private WebContent currentRegion;

	private ContentDelegateComposite compositeDelegate = new ContentDelegateComposite(
			resources);

	private ContentDelegate _dynamicPart = new ContentDelegate(Integer.MAX_VALUE) {
		public ComponentResources getComponentResources() {
			return resources;
		}

		public Block getBlock(String regionKey) {
			Block paramBlock = resources.getBlockParameter(regionKey);

			if (paramBlock != null) {
				return paramBlock;
			}

			currentRegions = webNodeType.getContentForRegionKey(regionKey);

			return (currentRegions.size() > 0) ? regionBlock : null;
		}
	};

	@BeginRender
	RenderCommand beginRender() {
		compositeDelegate.addDynamicDelegatePart(_dynamicPart);
		return template.createRenderCommand(compositeDelegate);
	}

	@AfterRender
	public void afterRender() {
		compositeDelegate.clear();
	}

	public String getRegionContent() {
		return webContentService.getParsedContent(this.currentRegion);
	}

	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(
				webNodeType.getLayoutKey(), "WebNode.tml");
		return template;
	}

	public WebNodeType getWebNodeType() {
		return webNodeType;
	}

	public void setWebNodeType(WebNodeType webNodeType) {
		this.webNodeType = webNodeType;
	}

	public void addContentDelegate(ContentDelegate delegate) {
		compositeDelegate.addDynamicDelegatePart(delegate);
	}
}
