package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

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
		HashMap<String, Zone> hashMap = new HashMap<String, Zone>();
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
		pageOptionsArea.refreshThemes();
		return pageOptionsArea.getOptionsAndButtonsZone();
	}

	public Object onActionFromNewOptions() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		newPageOptionsArea.refreshThemes();
		return newPageOptionsArea.getOptionsAndButtonsZone();
	}

	public Object onActionFromQuickJump() {
		if (request.getSession(false) == null) {
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

}