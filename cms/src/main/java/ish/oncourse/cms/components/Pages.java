package ish.oncourse.cms.components;

import ish.oncourse.cms.services.Constants;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

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

	public Object onActionFromNewPage() throws IOException {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		
		final Session session = request.getSession(false);
		session.setAttribute(WebNode.ADD_NEW_PAGE_ATTR, Boolean.TRUE);
		WebNode newPageNode = webNodeService.createNewNode();
		newPageNode.getObjectContext().commitChanges();

        Link link = pageRenderLinkSource.createPageRenderLinkWithContext(Page.class, newPageNode.getNodeNumber());
        link.addParameter("newpage", "y");
        return link.copyWithBasePath(String.format(Constants.PAGE_URL_TEMPLATE, newPageNode.getNodeNumber()));
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
