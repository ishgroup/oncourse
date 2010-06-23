package ish.oncourse.model;

import ish.oncourse.model.auto._WebNode;
import java.util.ArrayList;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;


public class WebNode extends _WebNode {

	static final String DEFAULT_PAGE_TITLE = "New Page";

	public String getPath() {
		if (getParent() == null || getParent() == getSite().getHomePage()) {
			return "/" + getUrlShortName();
		}

		return getParent().getPath() + "/" + getUrlShortName();
	}

	public String getUrlShortName() {
		String s = getShortName();
		if (s == null) {
			s = getName();
		}
		if (s == null) {
			s = DEFAULT_PAGE_TITLE;
		}
		return s.trim().replaceAll("\\s", "+");
	}

	@Override
    public List<WebNode> getChildren() {
		List<WebNode> children = super.getChildren();
		List<Ordering> orderings = new ArrayList<Ordering>();

		Ordering order = new Ordering();
		order.setSortSpecString(WebNode.WEIGHTING_PROPERTY);
		order.setAscending();

		orderings.add(order);

		Ordering.orderList(children, orderings);

		return children;
	}

	/**
	 * @return All web-navigable web-visible child nodes for this node.
	 */
	public List<WebNode> navigableChildNodes() {
		Expression expr = ExpressionFactory.matchExp(WebNode.WEB_NAVIGABLE_PROPERTY, true)
				.andExp(ExpressionFactory.matchExp(WebNode.WEB_VISIBLE_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.DELETED_PROPERTY, false)
						.orExp(ExpressionFactory.matchExp(WebNode.DELETED_PROPERTY, null))
				);

		return expr.filterObjects(getChildren());
	}
}
