package ish.oncourse.ui.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;
import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

public class ContentStructureTemplate {

	@Parameter
	private WebNode node;

	@Inject
	private ComponentResources resources;

	@Inject
	private IResourceService resourceService;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	@Property
	private Block regionBlock;

	@Property
	@Persist
	private WebContent currentRegion;

	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	@BeginRender
	RenderCommand beginRender() {
		return template.createRenderCommand(new DynamicDelegate() {
			public Block getBlock(final String regionKey) {
				RegionKey key = RegionKey.valueOf(regionKey.toLowerCase());
				List<WebContentVisibility> list = ExpressionFactory.matchExp(WebContentVisibility.REGION_KEY_PROPERTY, key).filterObjects(node.getWebContentVisibility());
				currentRegion = list.get(0).getWebContent();
				return regionBlock;
			}

			public ComponentResources getComponentResources() {
				return resources;
			}
		});
	}

	public String getRegionContent() {
		return currentRegion.accept(new ParsedContentVisitor(textileConverter));
	}

	public String getTemplateId() {
		return (webNodeService.getHomePage().getId() == this.node.getId()) ? IWebNodeService.WELCOME_TEMPLATE_ID : "";
	}

	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(node.getWebNodeType().getLayoutKey(), "ContentStructure.tml");
		return template;
	}
}
