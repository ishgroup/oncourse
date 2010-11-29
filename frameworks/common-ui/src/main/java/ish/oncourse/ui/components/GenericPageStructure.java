package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class GenericPageStructure {
	
	private static final String MSIE = "MSIE";

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

	@Component(parameters = { "webNodeType=webNodeType" })
	private BodyStructureTemplate bodyStructure;

	@SetupRender
	public void beforeRender() {
		if (!resources.isBound("webNodeType")) {
			this.webNodeType = webNodeTypeService.getDefaultWebNodeType();
		}
	}

	public String getAgentAwareBodyClass() {

		String userAgent = request.getHeader("User-Agent");
		if (userAgent.indexOf(MSIE) > -1) {
			int versionPosition = userAgent.indexOf(MSIE) + MSIE.length() + 1;
			Integer versionNumber = Integer.parseInt(userAgent.substring(versionPosition, versionPosition + 1));
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
}
