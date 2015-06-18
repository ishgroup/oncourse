package ish.oncourse.model;

import ish.oncourse.model.auto._WebMenu;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
	}

	/**
	 * @return all child menus including not navigable.
	 */
	@Deprecated //IWebMenuService.getChildrenBy should be used
	private List<WebMenu> getWebMenus() {
		List<WebMenu> children = getChildrenMenus();
		Collections.sort(children);
		return children;
	}

	/**
	 * @return All child menus with published nodes for this menu.
	 */
	@Deprecated //IWebMenuService.getNavigableChildrenBy should be used
	public List<WebMenu> getNavigableChildMenus() {

		Expression expr = ExpressionFactory
				.matchExp(WebMenu.WEB_NODE_PROPERTY + "." + WebNode.PUBLISHED_PROPERTY, true).orExp(
						ExpressionFactory.matchExp(WebMenu.WEB_NODE_PROPERTY, null).andExp(
								ExpressionFactory.noMatchExp(WebMenu.URL_PROPERTY, null))
				);

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

	public String getNonUniqueNameWarning() {
		return "The name is already used.";
	}

	public int compareTo(WebMenu o) {
		return this.getWeight() - o.getWeight();
	}
}
