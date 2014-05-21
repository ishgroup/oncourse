package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class MenuItem {

	@Parameter(required = true, cache = false)
	private WebMenu menu;

	@Parameter
	private int childPosition;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

    @Inject
    private IWebNodeService webNodeService;

	@SetupRender
	boolean setup() {
		// prevents rending with the menu parameter is null.
		return menu != null;
	}

	@BeforeRenderBody
	boolean beforeChild() {
		List<WebMenu> navigableChildMenus = getChildren();
		// if the menu has children, render the body to render a child
		final boolean render = navigableChildMenus.size() > 0;

		if (render) {
			// sets the container's currentMenu to the menu's child at the given
			// index.
			menu = navigableChildMenus.get(childPosition);
		}
		return render;
	}

	protected List<WebMenu> getChildren() {
		return menu.getNavigableChildMenus();
	}

	@AfterRenderBody
	boolean afterChild() {
		// increment the child position, afterRender on the child will have the
		// container's currentMenu set back to the menu before the body was
		// rendered.
		childPosition = childPosition + 1;
		// return true on last child index, finishing the iteration over the
		// children, otherwise re-render the body (to render the next child)
		return getChildren().size() <= childPosition;
	}

	@AfterRender
	void after() {
		// set the currentMenu to the parent after render (pop the stack)
		WebMenu webMenu = menu.getParentWebMenu();
		if (webMenu != null) {
			menu = webMenu;
		}
	}

	public WebMenu getMenu() {
		return menu;
	}

	public String getItemHref() {
		WebNode node = menu.getWebNode();

		if (node != null) {
			return "http://" + request.getServerName() + webNodeService.getPath(node);
		}

		return menu.getUrl();
	}

	public String getMenuItemClass() {
		String requestPath = request.getPath();
		WebNode node = menu.getWebNode();

		if (node != null && requestPath.endsWith(webNodeService.getPath(node))) {
			return messages.get("cssclass.activePage");
		}

		return "";
	}

}
