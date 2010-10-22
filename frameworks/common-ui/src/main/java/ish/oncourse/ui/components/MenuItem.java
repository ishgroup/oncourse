package ish.oncourse.ui.components;

import java.util.List;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class MenuItem {

	@Parameter(required = true, cache = false)
	private WebMenu menu;

	@Parameter
	private int childPosition;

	@Inject
	private Request request;

	@SetupRender
	boolean setup() {
		// prevents rending with the menu parameter is null.
		return menu != null;
	}

	@BeforeRenderBody
	boolean beforeChild() {
		List<WebMenu> navigableChildMenus = menu.getNavigableChildMenus();
		// if the menu has children, render the body to render a child
		final boolean render = navigableChildMenus.size() > 0;

		if (render) {
			// sets the container's currentMenu to the menu's child at the given
			// index.
			menu = navigableChildMenus.get(childPosition);
		}
		return render;
	}

	@AfterRenderBody
	boolean afterChild() {
		// increment the child position, afterRender on the child will have the
		// container's currentMenu set back to the menu before the body was
		// rendered.
		childPosition = childPosition + 1;
		// return true on last child index, finishing the iteration over the
		// children, otherwise re-render the body (to render the next child)
		return menu.getNavigableChildMenus().size() <= childPosition;
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
			String contextPath = request.getContextPath();
			return contextPath + node.getPath();
		}

		return menu.getUrl();
	}
}
