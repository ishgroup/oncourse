package ish.oncourse.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;

import ish.oncourse.model.auto._WebMenu;

public class WebMenu extends _WebMenu {

	public List<WebMenu> getWebMenus() {
		List<WebMenu> children = getChildrenMenus();
		List<Ordering> orderings = new ArrayList<Ordering>();

		Ordering order = new Ordering();
		order.setSortSpecString(WebMenu.WEIGHT_PROPERTY);
		order.setAscending();

		orderings.add(order);

		Ordering.orderList(children, orderings);

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

		return expr.filterObjects(getWebMenus());
	}

}
