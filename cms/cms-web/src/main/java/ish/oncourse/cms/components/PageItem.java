package ish.oncourse.cms.components;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageItem {
	
	@Parameter
	@Property
	private WebNode webNode;
	
	@Inject
	private Request request;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Property
	@Inject
	private IWebNodeService webNodeService;
	
	@InjectPage
	private Page page;
	
	@Inject
	private ComponentResources componentResources;
	
	public String getLastEdited() {
		return webNode.accept(new LastEditedVisitor());
	}
	
	public Object onActionFromDeletePage(Integer nodeNumber) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNode node = webNodeService.getNodeForNodeNumber(nodeNumber);
		if (node != null) {
			for (WebContentVisibility v : node.getWebContentVisibility()) {
				ctx.deleteObject(ctx.localObject(v.getWebContent().getObjectId(), null));
			}
			WebNode localNode = (WebNode) ctx.localObject(node.getObjectId(), null);
			ctx.deleteObject(localNode);
			ctx.commitChanges();
		}
		Pages parentComponent = (Pages) componentResources.getContainer();
		return parentComponent.getPagesListZone().getBody();
	}
	

}
