package ish.oncourse.cms.components;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Pages {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

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
		WebNode newPageNode = webNodeService.createNewNode();
		newPageNode.getObjectContext().commitChanges();

		return new URL("http://" + request.getServerName() + "/page/" + newPageNode.getNodeNumber());
	}

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

			// FIXME this should be done by using proper constraints and cascade
			// deletion in cayenne
			WebNode localNode = (WebNode) ctx.localObject(node.getObjectId(), null);
			localNode.setDefaultWebURLAlias(null);

			for (WebUrlAlias alias : node.getWebUrlAliases()) {
				WebUrlAlias localAlias = (WebUrlAlias) ctx.localObject(alias.getObjectId(), null);
				ctx.deleteObject(localAlias);
			}
			ctx.commitChanges();
			// end of FIXME

			ctx.deleteObject(localNode);
			ctx.commitChanges();
		}
		return pagesListZone.getBody();
	}

	public List<WebNode> getNodes() {
		Ordering ordering = new Ordering(WebNode.NAME_PROPERTY, SortOrder.ASCENDING);
		List<WebNode> result = webNodeService.getNodes();
		ordering.orderList(result);
		return result;
	}

	public Zone getPagesListZone() {
		return pagesListZone;
	}

}
