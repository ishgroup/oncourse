package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;

import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.internal.util.RenderableAsBlock;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;
import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

@SupportsInformalParameters
public class BodyStructureTemplate {

	@Inject
	private ComponentResources resources;
	
	@Inject
	@Path("BodyStructure.tml")
	private Asset bodyStructureTml;

	@Inject
	private IResourceService resourceService;

	@Inject
	private ITextileConverter textileConverter;

	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	@Parameter
	@Property
	private WebNodeType webNodeType;

	@BeginRender
	RenderCommand beginRender() {
		return template.createRenderCommand(new DynamicDelegate() {
			public Block getBlock(final String regionKey) {
				Block paramBlock = resources.getBlockParameter(regionKey);
				if (paramBlock != null) {
					return paramBlock;
				} else {
					return new RenderableAsBlock(new Renderable() {
						public void render(MarkupWriter writer) {
							List<WebContent> regions = webNodeType.getContentForRegionKey(regionKey);
							for (WebContent region : regions) {
								writer.writeRaw(region.accept(new ParsedContentVisitor(textileConverter)));
							}
						}
					});
				}
			}

			public ComponentResources getComponentResources() {
				return resources;
			}
		});
	}

	public Object getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(webNodeType.getLayoutKey(), "BodyStructure.tml");
		return (template == null) ? bodyStructureTml : template;
	}
}
