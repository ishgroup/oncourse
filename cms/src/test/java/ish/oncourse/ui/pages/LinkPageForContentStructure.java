package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class LinkPageForContentStructure {

	@Property
	private WebNode node;

	@SetupRender
	public void beforeRender() {

		try {
			ContextUtils.setupDataSources();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		ObjectContext context = ContextUtils.createObjectContext();

		node = context.newObject(WebNode.class);
		node.setName("New Page");
		
		WebSite webSite = context.newObject(WebSite.class); //(WebSite) context.localObject(webSiteService.getCurrentWebSite().getObjectId(), null);
		WebSiteVersion version = context.newObject(WebSiteVersion.class);
		version.setWebSite(webSite);
		
		node.setWebSiteVersion(webSite.getVersions().get(0));
		node.setNodeNumber(9999);

		WebContent webContent=context.newObject(WebContent.class);
		webContent.setContentTextile("{image name:\"TEST\"}");
		webContent.setContent("{image name:\"TEST\"}");
		webContent.setWebSiteVersion(webSite.getVersions().get(0));
		
		WebContentVisibility webContentVisibility = context.newObject(WebContentVisibility.class);
		webContentVisibility.setWebNode(node);
		webContentVisibility.setRegionKey(RegionKey.content);
		webContentVisibility.setWebContent(webContent);
	}
}
