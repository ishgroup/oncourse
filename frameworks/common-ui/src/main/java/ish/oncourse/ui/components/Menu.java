package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Menu {
	@Property
	@Parameter(required = true)
	private WebMenu mainMenu;

	@Property
	@Component(id = "menuItem", parameters = { "menu=currentMenu",
			"childPosition=currentChildPosition" })
	private MenuItem menuItem;

	private WebMenu currentMenu;

	private Map<WebMenu, Integer> childPositions;
	
	public WebMenu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(final WebMenu menu) {
        if (!childPositions.containsKey(menu)) {
            childPositions.put(menu, 0);
        }
        this.currentMenu = menu;
    }
	
	public int getCurrentChildPosition() {
		return childPositions.get(this.currentMenu);
	}

	public void setCurrentChildPosition(final int pos) {
		this.childPositions.put(currentMenu, pos);
	}
	
	void setupRender() {
        childPositions = new HashMap<WebMenu, Integer>();
    }
}
