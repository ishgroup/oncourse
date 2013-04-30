package ish.oncourse.model;

import ish.oncourse.model.auto._WebMenu;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class WebMenu extends _WebMenu implements Comparable<WebMenu> {
	private static final long serialVersionUID = -6977235713134006203L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WebMenu> getChildrenMenus() {
		
		if (!getObjectId().isTemporary()) {
			Expression expr = ExpressionFactory.matchExp(WebMenu.PARENT_WEB_MENU_PROPERTY, this);
			SelectQuery q = new SelectQuery(WebMenu.class, expr);
			q.addPrefetch(WebMenu.PARENT_WEB_MENU_PROPERTY);
			q.addPrefetch(WebMenu.CHILDREN_MENUS_PROPERTY);
			return getObjectContext().performQuery(q);
		}
		
		return super.getChildrenMenus();
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
		StringBuilder stringBuffer = new StringBuilder("Warning! " + getName() + " is referred to ");
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

		Integer oldWeight = getWeight();
		setWeight(weight);
		List<WebMenu> siblings = getParentWebMenu().getWebMenus();
		if (oldWeight == null) {
			oldWeight = siblings.size();
		}
		// if we drag menu from another parent, we should update the weights in
		// old tree
		if (oldParent != null && !oldParent.getId().equals(getParentWebMenu().getId())) {
			List<WebMenu> oldSiblings = oldParent.getWebMenus();
			for (int i = 0; i < oldSiblings.size(); i++) {
				oldSiblings.get(i).setWeight(i);
			}
			oldWeight = siblings.size();
		}
		
		for (int i = 0; i < siblings.size(); i++) {
			WebMenu m = siblings.get(i);
			if (m.getPersistenceState() != PersistenceState.NEW && !m.getId().equals(getId())) {
				if (m.getWeight() == weight) {
					if (i < weight) {
						if (oldWeight > weight) {
							m.setWeight(i + 1);
						} else {
							m.setWeight(i);
						}
					} else {
						if (i == weight) {
							if (oldWeight > weight) {
								m.setWeight(i + 1);
							} else {
								m.setWeight(i - 1);
							}
						} else {
							m.setWeight(i);
						}
					}
				} else {
					m.setWeight(i);
				}
			}
		}
	}
}
