package ish.oncourse.cms.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class MenuNode extends ish.oncourse.ui.components.MenuItem {

    @Inject
    private IWebNodeService webNodeService;

	public String getItemHref() {
		WebNode node = getMenu().getWebNode();

		if (node != null) {
			return webNodeService.getPath(node);
		}

		return getMenu().getUrl();
	}

	protected List<WebMenu> getChildren() {
		return getMenu().getWebMenus();
	}
}
