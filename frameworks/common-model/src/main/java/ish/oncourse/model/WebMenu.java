package ish.oncourse.model;

import ish.oncourse.model.auto._WebMenu;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class WebMenu extends _WebMenu implements Comparable<WebMenu> {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
		setWeight(0);
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

		Expression expr = ExpressionFactory.matchExp(
				WebMenu.WEB_NODE_PROPERTY + "." + WebNode.PUBLISHED_PROPERTY,
				true).orExp(
				ExpressionFactory.matchExp(WebMenu.WEB_NODE_PROPERTY, null)
						.andExp(ExpressionFactory.noMatchExp(
								WebMenu.URL_PROPERTY, null)));

		List<WebMenu> list = expr.filterObjects(getWebMenus());

		return list;
	}

	public int compareTo(WebMenu o) {
		return this.getWeight() - o.getWeight();
	}


    public void updateWeight(int weight) {

        for (int i = 0; i < getParentWebMenu().getChildrenMenus().size(); i++) {
			WebMenu m = getParentWebMenu().getChildrenMenus().get(i);

			if (m.getWeight() < weight) {
				m.setWeight(i);
			}
			else {
				m.setWeight(i + 1);
			}
		}

		setWeight(weight);
    }
}
