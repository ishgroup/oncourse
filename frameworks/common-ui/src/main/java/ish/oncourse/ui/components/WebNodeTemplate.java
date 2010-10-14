package ish.oncourse.ui.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.RenderCommand;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;
import com.howardlewisship.tapx.core.dynamic.DynamicTemplate;

public class WebNodeTemplate {

	@Parameter(required = true, allowNull = false)
	private WebNode node;
	
	@Parameter(required = true)
	private DynamicDelegate blockSource;
	
	@Parameter("selectedTemplate")
	private DynamicTemplate template;

	@Inject
	private IResourceService resourceService;

	public PrivateResource getSelectedTemplate() {
		PrivateResource template = resourceService.getTemplateResource(node
				.getWebNodeType().getLayoutKey(), "WebNode.tml");
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
