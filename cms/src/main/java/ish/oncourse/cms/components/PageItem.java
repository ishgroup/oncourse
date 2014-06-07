package ish.oncourse.cms.components;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

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
		
	public String getLastEdited() {
		return webNode.accept(new LastEditedVisitor());
	}
		
	StreamResponse onActionFromDeletePage(Integer nodeNumber) {
		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNode node = webNodeService.getNodeForNodeNumber(nodeNumber);
		if (node != null) {
			for (WebContentVisibility v : node.getWebContentVisibility()) {
				ctx.deleteObjects(ctx.localObject(v.getWebContent()));
			}
			WebNode localNode = ctx.localObject(node);
			ctx.deleteObjects(localNode);
			ctx.commitChanges();
		}
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}


    public String getPath()
    {
        return webNodeService.getPath(webNode);
    }
	

}
