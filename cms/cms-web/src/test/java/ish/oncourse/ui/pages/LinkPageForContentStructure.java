package ish.oncourse.ui.pages;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ContextUtils;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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
		node.setWebSite(webSite);
		node.setNodeNumber(9999);

		WebContent webContent=context.newObject(WebContent.class);
		webContent.setContentTextile("{image}");
		webContent.setContent("{image}");
		webContent.setWebSite(webSite);
		
		WebContentVisibility webContentVisibility = context.newObject(WebContentVisibility.class);
		webContentVisibility.setWebNode(node);
		webContentVisibility.setRegionKey(RegionKey.content);
		webContentVisibility.setWebContent(webContent);
	}
}
