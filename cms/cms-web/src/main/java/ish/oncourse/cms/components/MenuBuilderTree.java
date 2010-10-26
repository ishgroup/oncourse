package ish.oncourse.cms.components;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;

public class MenuBuilderTree extends ish.oncourse.ui.components.Menu {
	@Property
	@Component(id = "menuItem", parameters = { "menu=currentMenu",
			"childPosition=currentChildPosition" })
	private MenuBuilderNode menuItem;
}
