package ish.oncourse.cms.components;

import java.util.List;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;

public class MenuNode extends ish.oncourse.ui.components.MenuItem {

	public String getItemHref() {
		WebNode node = getMenu().getWebNode();

		if (node != null) {
			return node.getPath();
		}

		return getMenu().getUrl();
	}

	protected List<WebMenu> getChildren() {
		return getMenu().getWebMenus();
	}
}
