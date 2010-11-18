package ish.oncourse.ui.components;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.dynamic.ContentDelegate;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * A page wrapper component.
 */
@SupportsInformalParameters
public class PageWrapper {
	
	private static final String MSIE = "MSIE";
	
	@Inject
	private IAuthenticationService authenticationService;
	
	@Property
	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private IWebNodeTypeService webNodeTypeService;
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebMenuService webMenuService;

	@Inject
	private Request request;

	@Inject
	private ComponentResources resources;

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

	@Parameter
	private ContentDelegate delegate;

	@Component
	private BodyLayout bodyLayout;

	private ContentDelegate _dynamicPart = new ContentDelegate(1) {
		public ComponentResources getComponentResources() {
			return resources;
		}

		public Block getBlock(String regionKey) {
			return resources.getBlockParameter(regionKey);
		}
	};

	@SetupRender
	public void beforeRender() {
		bodyLayout.addContentDelegate(_dynamicPart);
		if (resources.isBound("delegate")) {
			bodyLayout.addContentDelegate(delegate);
		}

		webNodeType = (resources.isBound("webNodeType")) ? webNodeType
				: webNodeTypeService.getDefaultWebNodeType();

		bodyLayout.setWebNodeType(webNodeType);
	}

	public String getAgentAwareBodyClass() {

		String userAgent = request.getHeader("User-Agent");
		if (userAgent.indexOf(MSIE) > -1) {
			int versionPosition = userAgent.indexOf(MSIE) + MSIE.length() + 1;
			Integer versionNumber = Integer.parseInt(userAgent.substring(
					versionPosition, versionPosition + 1));
			switch (versionNumber) {
			case 7:
				return bodyClass + " ie7";
			case 8:
				return bodyClass + " ie8";
			case 9:
				return bodyClass + " ie9";
			default:
				if (versionNumber < 7) {
					return bodyClass + " ie6";
				}
				if (versionNumber > 9) {
					return bodyClass;
				}
			}

		}

		return bodyClass;
	}

	public WebMenu getMenu() {
		return webMenuService.getRootMenu();
	}

	public String getCollegeName() {
		return webSiteService.getCurrentCollege().getName();
	}

	public String getHomeLink() {
		return webSiteService.getHomeLink();
	}
	
	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}
	
	public boolean isHasCurrentNode() {
		return webNodeService.getCurrentNode() != null;
	}
}
