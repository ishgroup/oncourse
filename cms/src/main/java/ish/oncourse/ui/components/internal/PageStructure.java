package ish.oncourse.ui.components.internal;

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

	private static final String USER_AGENT_HEADER = "User-Agent";

	private static final String WEB_NODE_TYPE = "webNodeType";

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@SuppressWarnings("all")
	@Property
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private Request request;

	@Inject
	private ComponentResources resources;

	@Parameter
	private String bodyClass;

	@SuppressWarnings("all")
	@Property
	@Parameter
	private String bodyId;

	@SuppressWarnings("all")
	@Property
	@Parameter
	private WebNodeType webNodeType;

	@SuppressWarnings("all")
	@Property
	@Parameter
	private WebNode node;

	@SuppressWarnings("all")
	@Property
	@Parameter
	private String title;

	@Inject
	private IAuthenticationService authenticationService;

	@SuppressWarnings("all")
	@InjectComponent
	@Property
	private CmsNavigation cmsNavigation;

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null || authenticationService.getSystemUser() != null;
	}

	@SetupRender
	public void beforeRender() {
		if (!resources.isBound(WEB_NODE_TYPE)) {
			this.webNodeType = webNodeTypeService.getDefaultWebNodeType();
		}
	}

	public String getAgentAwareBodyClass() {
		return bodyClass + RequestUtil.getAgentAwareClass(request.getHeader(USER_AGENT_HEADER));
	}


}
