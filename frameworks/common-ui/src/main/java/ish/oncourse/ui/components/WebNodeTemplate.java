package ish.oncourse.ui.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeContent;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.internal.util.RenderableAsBlock;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;
import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

public class WebNodeTemplate {

	@Parameter(required = true, allowNull = false)
	private WebNode node;

	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	@Parameter("BlockSource")
	private DynamicDelegate blockSource;

	@Inject
	private IResourceService resourceService;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private ITextileConverter textileConverter;

	public DynamicDelegate getBlockSource() {
		final List<WebNodeContent> contents = node.getWebNodeContents();

		return new DynamicDelegate() {
			public Block getBlock(final String regionKey) {

				Block block = componentResources.getBlockParameter(regionKey);

				if (block != null) {
					return block;
				}

				final Expression expr = ExpressionFactory.matchExp(
						WebNodeContent.REGION_KEY_PROPERTY, regionKey);

				final WebNodeContent nodeContent = expr.filterObjects(contents)
						.get(0);

				return new RenderableAsBlock(new Renderable() {
					public void render(MarkupWriter writer) {
						String text = nodeContent.getContent();

						Pattern pattern = Pattern
								.compile(TextileUtil.TEXTILE_REGEXP);

						Matcher matcher = pattern.matcher(text);

						if (matcher.find()) {
							ValidationErrors errors = new ValidationErrors();
							text = textileConverter.convert(text, errors);
							if (errors.hasFailures()) {
								try {
									throw new ValidationException(errors);
								} catch (ValidationException e) {
									e.printStackTrace();
								}
							}
						}

						writer.writeRaw("<div id=\"" + regionKey +  "\" class=\"region\">");
						writer.writeRaw(text);
						writer.writeRaw("</div>");
					}
				});
			}

			// TODO: Implement - this is due to the recent API change in tapX
			public Object getExpressionRoot() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public ComponentResources getComponentResources() {
				return null;
			}
		};
	}

	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(node
				.getWebNodeType().getTemplateKey(), "WebNode.tml");
		return template;
	}

	RenderCommand beginRender() {
		return template.createRenderCommand(blockSource);
	}

	public DynamicTemplate getTemplate() {
		return template;
	}

	public void setTemplate(DynamicTemplate template) {
		this.template = template;
	}

	/**
	 * @return the node
	 */
	public WebNode getNode() {
		return node;
	}
}
