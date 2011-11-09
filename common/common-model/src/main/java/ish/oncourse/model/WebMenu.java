package ish.oncourse.model;

import ish.oncourse.model.auto._WebMenu;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class WebMenu extends _WebMenu implements Comparable<WebMenu> {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
		setName("New menu item");
	}

	/**
	 * 
	 * @return all child menus including not navigable.
	 */
	public List<WebMenu> getWebMenus() {
		List<WebMenu> children = getChildrenMenus();
		Collections.sort(children);
		return children;
	}

	/**
	 * @return All child menus with published nodes for this menu.
	 */
	public List<WebMenu> getNavigableChildMenus() {

		Expression expr = ExpressionFactory
				.matchExp(WebMenu.WEB_NODE_PROPERTY + "." + WebNode.PUBLISHED_PROPERTY, true).orExp(
						ExpressionFactory.matchExp(WebMenu.WEB_NODE_PROPERTY, null).andExp(
								ExpressionFactory.noMatchExp(WebMenu.URL_PROPERTY, null)));

		List<WebMenu> list = expr.filterObjects(getWebMenus());

		return list;
	}

	public String getWarning() {
		StringBuffer stringBuffer = new StringBuffer("Warning! " + getName() + " is referred to ");
		WebNode webNode = getWebNode();
		if (webNode == null) {
			if (getUrl() == null) {
				stringBuffer.append("an empty url ");
			} else {
				return "";
			}
		} else {
			if (webNode.isPublished()) {
				return "";
			} else {
				stringBuffer.append("unpublished page ");
			}
		}
		stringBuffer.append("and will not be shown in the main menu!");
		return stringBuffer.toString();
	}

	public int compareTo(WebMenu o) {
		return this.getWeight() - o.getWeight();
	}

	public synchronized void updateWeight(int weight, WebMenu oldParent) {
		setWeight(weight);
		
		//if we drag menu from another parent, we should update the weights in old tree
		if (oldParent != null && !oldParent.getId().equals(getParentWebMenu().getId())) {
			List<WebMenu> oldSiblings = oldParent.getWebMenus();
			for (int i = 0; i < oldSiblings.size(); i++) {
				oldSiblings.get(i).setWeight(i);
			}
		}
		//all the menus in the right order
		List<WebMenu> siblings = getParentWebMenu().getWebMenus();
		//flag which points if we already passed the dragged item or not: important for the setting weight to the item which has the same weight.
		boolean passedDropped = false;
		for (int i = 0; i < siblings.size(); i++) {
			WebMenu m = siblings.get(i);
			if (m.getPersistenceState() != PersistenceState.NEW && !m.getId().equals(getId())) {
				if (m.getWeight() == weight && !passedDropped) {
					m.setWeight(i + 1);
				} else {
					m.setWeight(i);
				}
			} else {
				passedDropped = true;
			}
		}
	}
}
