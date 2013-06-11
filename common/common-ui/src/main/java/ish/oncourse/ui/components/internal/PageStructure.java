package ish.oncourse.ui.components.internal;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.util.RequestUtil;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * A page structure component.
 */
public class PageStructure {

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private Request request;

	@Inject
	private ComponentResources resources;

	@Inject
	private ICacheMetaProvider cacheMetaProvider;

	@Property
	@Parameter
	private String bodyId;

	@Parameter
	private String bodyClass;

	@Property
	@Parameter
	private WebNodeType webNodeType;

	@Property
	@Parameter
	private String title;
	
	@Property
	@Parameter
	private String canonicalLinkPath;

	@Property
	@Parameter
	private String metaDescription;

	@SetupRender
	public void beforeRender() {
		if (!resources.isBound("webNodeType")) {
			this.webNodeType = webNodeTypeService.getDefaultWebNodeType();
		}
	}

	public String getAgentAwareBodyClass() {
		return bodyClass + RequestUtil.getAgentAwareClass(request.getHeader("User-Agent"));
	}

	public boolean isWrapped() {
		String wrap = request.getParameter("wrap");
		return wrap == null || Boolean.parseBoolean(wrap);
	}

	public String getCacheMeta()
	{
		return cacheMetaProvider.getMetaContent();
	}
}
