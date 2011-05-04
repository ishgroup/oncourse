package ish.oncourse.ui.components;

import ish.oncourse.cms.components.CmsNavigation;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.RequestUtil;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
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
	
	@Property
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private Request request;

	@Inject
	private ComponentResources resources;

	@Parameter
	private String bodyClass;

	@Property
	@Parameter
	private String bodyId;

	@Property
	@Parameter
	private WebNodeType webNodeType;

	@Property
	@Parameter
	private WebNode node;

	@Property
	@Parameter
	private String title;

	@Inject
	private IAuthenticationService authenticationService;

	@InjectComponent
	@Property
	private CmsNavigation cmsNavigation;

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}

	@SetupRender
	public void beforeRender() {
		if (!resources.isBound("webNodeType")) {
			this.webNodeType = webNodeTypeService.getDefaultWebNodeType();
		}
	}

	public String getAgentAwareBodyClass() {
		return bodyClass + RequestUtil.getAgentAwareClass(request.getHeader("User-Agent"));
	}
}
