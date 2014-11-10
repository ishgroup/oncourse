package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.util.List;

public class Pages {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private Request request;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

	@InjectPage
	private Page page;

	@Property
	private WebNode webNode;

	@InjectComponent
	private Zone pagesListZone;
	
	private static String JSON_PATH_KEY = "path";

	@OnEvent(value = "newPage")
	public Object newPage() throws IOException {
		final Session session = request.getSession(false);
		if (session == null) {
			return page.getReloadPageBlock();
		}
		

		session.setAttribute(WebNode.ADD_NEW_PAGE_ATTR, Boolean.TRUE);
		WebNode newPageNode = webNodeService.createNewNode();
		
		try {
			newPageNode.getObjectContext().commitChanges();
		} catch (CayenneRuntimeException e) {
			newPageNode.getObjectContext().rollbackChanges();
			return null;
		}
       
		JSONObject jUrl = new JSONObject();
		jUrl.put(JSON_PATH_KEY, "/page/" + newPageNode.getNodeNumber());
		
		return new TextStreamResponse("text/json", jUrl.toString());
		
	}

	public List<WebNode> getNodes() {
		Ordering ordering = new Ordering(WebNode.MODIFIED_PROPERTY, SortOrder.DESCENDING);
		List<WebNode> result = webNodeService.getNodes();
		ordering.orderList(result);
		return result;
	}

	public Zone getPagesListZone() {
		return pagesListZone;
	}

}
