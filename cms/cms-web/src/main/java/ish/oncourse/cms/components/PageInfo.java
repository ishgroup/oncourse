package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Parameter
	@Property
	private WebNode node;

	@Parameter
	@Property
	private WebNode newPageNode;

	@Inject
	private Messages messages;

	@Property
	@Component
	private Zone publishedZone;

	@Component
	private Zone updatedZone;

	@SetupRender
	public void beforeRender() {
		initNewPageNode();
	}

	public String getPageStatus() {
		String key = "status." + (node.isPublished() ? "published" : "nopublished");
		return messages.get(key);
	}

	public String getLastEdited() {
		return node.accept(new LastEditedVisitor(messages));
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
		ObjectContext ctx = cayenneService.newContext();

		newPageNode = ctx.newObject(WebNode.class);
		newPageNode.setName("New Page");
		newPageNode.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite()
				.getObjectId(), null));
		newPageNode.setNodeNumber(webNodeService.getNextNodeNumber());

		newPageNode.setWebNodeType((WebNodeType) ctx.localObject(webNodeTypeService
				.getDefaultWebNodeType().getObjectId(), null));

		WebContentVisibility contentVisibility = ctx.newObject(WebContentVisibility.class);
		contentVisibility.setRegionKey(RegionKey.content);

		WebContent webContent = ctx.newObject(WebContent.class);
		webContent.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite()
				.getObjectId(), null));
		webContent.setContent("Sample content text.");
		contentVisibility.setWebContent(webContent);

		newPageNode.addToWebContentVisibility(contentVisibility);
	}
}
