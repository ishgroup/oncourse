package ish.oncourse.cms.pages;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.pages.Page;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NewPage extends Page {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebNodeService webNodeService;

	@SetupRender
	public void beforeRender() {
		
	}

	private WebNode newPage() {
		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);

		newPageNode.setName("New Page");

		WebSite webSite = (WebSite) ctx.localObject(webSiteService
				.getCurrentWebSite().getObjectId(), null);

		newPageNode.setWebSite(webSite);
		newPageNode.setNodeNumber(webNodeService.getNextNodeNumber());

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

		return newPageNode;
	}
}
