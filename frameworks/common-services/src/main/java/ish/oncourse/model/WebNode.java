package ish.oncourse.model;

import ish.oncourse.model.auto._WebNode;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

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

	/**
	 * @return All web-navigable web-visible child nodes for this node.
	 */
	public List<WebNode> navigableChildNodes() {
		Expression expr = ExpressionFactory.matchExp(
				WebNode.WEB_NAVIGABLE_PROPERTY, true).orExp(
				ExpressionFactory
						.matchExp(WebNode.WEB_NAVIGABLE_PROPERTY, null))
				.andExp(
						ExpressionFactory.matchExp(
								WebNode.WEB_VISIBLE_PROPERTY, true).orExp(
								ExpressionFactory.matchExp(
										WebNode.WEB_VISIBLE_PROPERTY, null)));

		return expr.filterObjects(getChildren());
	}
}
