package ish.oncourse.ui.components;

import ish.oncourse.model.WebNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Menu {
	@Property
	@Parameter(required = true)
	private List<WebNode> nodes;

	@Property
	@Component(id = "menuItem", parameters = { "node=currentNode",
			"childPosition=currentChildPosition" })
	private MenuItem menuItem;

	private WebNode currentNode;

	private Map<WebNode, Integer> childPositions;
	
	public WebNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(final WebNode node) {
        if (!childPositions.containsKey(node)) {
            childPositions.put(node, 0);
        }
        this.currentNode = node;
    }
	
	public int getCurrentChildPosition() {
		return childPositions.get(this.currentNode);
	}

	public void setCurrentChildPosition(final int pos) {
		this.childPositions.put(currentNode, pos);
	}
	
	void setupRender() {
        childPositions = new HashMap<WebNode, Integer>();
    }
}
