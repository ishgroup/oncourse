package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.dynamic.DynamicDelegateComposite;
import ish.oncourse.ui.dynamic.DynamicDelegatePart;

import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
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

	@Parameter(required = true)
	private DynamicDelegateComposite dynamicDelegate;

	@Parameter
	private WebNodeType webNodeType;

	@Property
	private List<WebContent> currentRegions;

	@Property
	private WebContent currentRegion;

	private DynamicDelegatePart _dynamicPart = new DynamicDelegatePart(3) {
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

	RenderCommand beginRender() {
		dynamicDelegate.addDynamicDelegatePart(_dynamicPart);
		return template.createRenderCommand(dynamicDelegate);
	}

	public String getRegionContent() {
		return webContentService.getParsedContent(this.currentRegion);
	}

	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(
				webNodeType.getLayoutKey(), "WebNode.tml");
		return template;
	}
}
