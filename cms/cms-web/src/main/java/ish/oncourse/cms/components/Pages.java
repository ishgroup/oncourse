package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;

import java.io.IOException;
import java.net.URL;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Pages {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Property
	private WebNode webNode;

	@InjectComponent
	private Zone pagesListZone;

	public Object onActionFromNewPage() throws IOException {

		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);
		newPageNode.setName("New Page");
		newPageNode.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite()
				.getObjectId(), null));
		newPageNode.setNodeNumber(webNodeService.getNextNodeNumber());

		newPageNode.setWebNodeType((WebNodeType) ctx.localObject(webNodeTypeService
				.getDefaultWebNodeType().getObjectId(), null));

		ctx.commitChanges();

		return new URL("http://" + request.getServerName() + "/page/" + newPageNode.getNodeNumber());
	}

	public String getLastEdited() {
		return webNode.accept(new LastEditedVisitor(messages));
	}

	public Object onActionFromDeletePage(Integer nodeNumber) {
		ObjectContext ctx = cayenneService.newContext();
		WebNode node = webNodeService.getNodeForNodeNumber(nodeNumber);
		if (node != null) {
			ctx.deleteObject(ctx.localObject(node.getObjectId(), null));
			ctx.commitChanges();
		}
		return pagesListZone.getBody();
	}
}
