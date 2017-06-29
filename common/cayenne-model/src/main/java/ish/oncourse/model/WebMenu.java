package ish.oncourse.model;

import ish.oncourse.model.auto._WebMenu;
import ish.oncourse.utils.QueueableObjectUtils;

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
