package ish.oncourse.cms.components;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.util.HTMLUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class PageItem {

    private static final String JSON_template = "{status: '%s', message: '%s'}";

    public enum JSONStatus
    {
        ERROR,
        WARNING,
        OK
    }

    //returns the json when an user uses not ajax request.
    private static final String WARNING_invalidRequest = "invalid request";

    //retunrs the json when the session was invalidated
    private static final String ERROR_sessionTimeout = "session timeout";

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
			return new TextStreamResponse("text/json", String.format(JSON_template, JSONStatus.ERROR, ERROR_sessionTimeout));
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
		return new TextStreamResponse("text/json", String.format(JSON_template, JSONStatus.OK, JSONStatus.OK));
	}


    public String getPath()
    {
        return webNodeService.getPath(webNode);
    }


    @OnEvent(value = "changeVisibility" )
    public StreamResponse changeVisibility()
    {
        if (!request.isXHR()) {
            return new TextStreamResponse("text/json", String.format(JSON_template, JSONStatus.WARNING, WARNING_invalidRequest));
        }
        if (request.getSession(false) == null) {
            return new TextStreamResponse("text/json", String.format(JSON_template, JSONStatus.ERROR, ERROR_sessionTimeout));
        }

        String value = request.getParameter(WebNode.PUBLISHED_PROPERTY);

        boolean published = HTMLUtils.parserBooleanValue(value);

        value = request.getParameter(WebNode.ID_PK_COLUMN);
        if (StringUtils.isNumeric(value)) {
            long id = Long.valueOf(value);
            ObjectContext ctx = cayenneService.newContext();
            WebNode node = Cayenne.objectForPK(ctx, WebNode.class, id);
            if (node != null) {
                node.setPublished(published);
                ctx.commitChanges();
            }
        }
        return new TextStreamResponse("text/json", String.format(JSON_template, JSONStatus.OK, published));
    }
	

}
