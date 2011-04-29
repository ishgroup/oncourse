package ish.oncourse.cms.components;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.visitor.LastEditedVisitor;

import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {

	@Parameter
	@Property
	private WebNode node;

	@Inject
	private Messages messages;

	@Property
	@Component
	private Zone publishedZone;

	@Component
	private Zone updatedZone;

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
}
