package ish.oncourse.cms.components;

import java.io.IOException;
import java.net.URL;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;

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

	public Object onActionFromNewPage() throws IOException {
		
		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);
		newPageNode.setName("New Page");
		newPageNode.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		newPageNode.setNodeNumber(webNodeService.getNextNodeNumber());

		newPageNode.setWebNodeType((WebNodeType) ctx.localObject(webNodeTypeService.getDefaultWebNodeType().getObjectId(), null));

		ctx.commitChanges();
		
		return new URL("http://" + request.getServerName() + "/page/" + newPageNode.getNodeNumber());
	}

	public String getLastEdited() {
		return webNode.accept(new LastEditedVisitor(messages));
	}
}
