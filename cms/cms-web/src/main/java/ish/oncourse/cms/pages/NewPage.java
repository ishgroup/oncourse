package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NewPage extends EditPage {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@SetupRender
	public void beforeRender() {

		if (super.currentNode() != null) {
			return;
		}

		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);

		WebSite webSite = webSiteService.getCurrentWebSite();

		newPageNode.setWebSite((WebSite) ctx.localObject(webSite.getObjectId(),
				null));
		newPageNode.setWeighting(0);
		newPageNode.setNodeNumber(0);
		newPageNode.setWebNodeType(WebNodeType.forName(ctx, WebNodeType.PAGE));

		WebNodeContent content = ctx.newObject(WebNodeContent.class);
		content.setRegionKey("content");
		content.setContent("Sample content text.");

		newPageNode.addToWebNodeContents(content);

		super.selectNode(newPageNode);
	}
}
