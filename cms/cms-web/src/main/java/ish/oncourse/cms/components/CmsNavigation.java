package ish.oncourse.cms.components;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.Protected;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;

import java.io.IOException;
import java.net.URL;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@Protected
public class CmsNavigation {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IAuthenticationService authenticationService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private Request request;
	
	@Property
	@Persist
	private WebNode node;
	
	@SetupRender
	public void beforeRender() {
		this.node = webNodeService.getCurrentNode();
	}

	public boolean isHasCurrentNode() {
		return this.node != null;
	}

	public Object onActionFromLogout() throws IOException {
		authenticationService.logout();
		return null;
	}
	
	public Object onActionFromNewPage() throws IOException {
		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);

		newPageNode.setName("New Page");

		WebSite webSite = (WebSite) ctx.localObject(webSiteService
				.getCurrentWebSite().getObjectId(), null);

		newPageNode.setWebSite(webSite);
		Integer nodeNumber = webNodeService.getNextNodeNumber();
		
		newPageNode.setNodeNumber(nodeNumber);

		WebNodeType webNodeType = (WebNodeType) ctx.localObject(webNodeService
				.getDefaultWebNodeType().getObjectId(), null);

		newPageNode.setWebNodeType(webNodeType);

		WebContentVisibility contentVisibility = ctx
				.newObject(WebContentVisibility.class);

		contentVisibility.setRegionKey(WebContentVisibility.DEFAULT_REGION_KEY);
		contentVisibility.setWebNode(newPageNode);

		WebContent webContent = ctx.newObject(WebContent.class);
		webContent.setWebSite(webSite);
		webContent.setContent("Sample content text.");
		contentVisibility.setWebContent(webContent);

		ctx.commitChanges();
		
		return new URL("http://" + request.getServerName() + "/page/" + nodeNumber);
	}
}
