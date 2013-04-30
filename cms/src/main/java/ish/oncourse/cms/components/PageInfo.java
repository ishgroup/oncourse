package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageInfo {

	@InjectPage
	private Page page;

	@Inject
	private Request request;

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Parameter
	@Property
	private WebNode node;

	private List<WebNode> nodes;

	@SuppressWarnings("all")
	@Property
	private WebNode newPageNode;

	@Property
	private WebNode webNode;

	@Property
	@Component
	private Zone publishedZone;

	@InjectComponent
	@Property
	private PageOptions pageOptionsArea;

	@InjectComponent
	@Property
	private PageOptions newPageOptionsArea;

	@Component
	private Zone currentPageZone;

	@Component
	private Zone updatedZone;

	@Component
	private Zone pagesList;

	@SetupRender
	public void beforeRender() {
		initNewPageNode();
	}

	public String getStatusForNode() {
		if (node.getId().equals(webNode.getId())) {
			return "active";
		}
		return "";
	}

	public String getLastEdited() {
		return node.accept(new LastEditedVisitor());
	}

	public boolean isPageHasChanges() {
		for (WebContentVisibility w : node.getWebContentVisibility()) {
			WebContent content = w.getWebContent();
			if (PersistenceState.MODIFIED == content.getPersistenceState()) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Zone> getZonesForUpdate() {
		HashMap<String, Zone> hashMap = new HashMap<>();
		hashMap.put("publishedZone", publishedZone);
		hashMap.put("updatedZone", updatedZone);
		hashMap.put("currentPageZone", currentPageZone);
		return hashMap;
	}

	/**
	 * @return the updatedZone
	 */
	public Zone getUpdatedZone() {
		return updatedZone;
	}

	/**
	 * @param updatedZone
	 *            the updatedZone to set
	 */
	public void setUpdatedZone(Zone updatedZone) {
		this.updatedZone = updatedZone;
	}

	private void initNewPageNode() {
		newPageNode = webNodeService.createNewNode();
	}

	public Object onActionFromOptions() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		if (node == null || node.getObjectContext() == null) {
			//if current page have been deleted, so home page set as current. required for #14484.
			node = webNodeService.getHomePage();//node.getPath();
			pageOptionsArea.initPageWithNode(node);
		}
		pageOptionsArea.refreshThemes();
		return pageOptionsArea.getOptionsAndButtonsZone();
	}
	
	private boolean isSessionAndEntityValid() {
		return (request.getSession(false) != null && node != null && node.getObjectContext() != null);
	}

	public Object onActionFromNewOptions() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		newPageOptionsArea.refreshThemes();
		return newPageOptionsArea.getOptionsAndButtonsZone();
	}

	public Object onActionFromQuickJump() {
		if (!isSessionAndEntityValid()) {
			return page.getReloadPageBlock();
		}
		return pagesList.getBody();
	}

	public List<WebNode> getNodes() {
		nodes = webNodeService.getNodes();
		Ordering ordering = new Ordering(WebNode.NAME_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(nodes);
		return nodes;
	}

	Zone getCurrentPageZone() {
		return currentPageZone;
	}

}