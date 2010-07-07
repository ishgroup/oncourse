package ish.oncourse.ui.components;

import java.net.URLEncoder;

import ish.oncourse.model.WebNode;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;

public class MenuItem {

    @Parameter(required = true, cache = false)
    private WebNode node;

    @Parameter
    private int childPosition;

    @SetupRender
    boolean setup() {
        // prevents rending with the node parameter is null.
        return node != null;
    }

    @BeforeRenderBody
    boolean beforeChild() {
        // the node  has children, render the body to render a child
        final boolean render = node.getNavigableChildNodes().size() > 0;

        if (render) {
            // sets the container's currentNode to the node's child at the given index.
            node = node.getNavigableChildNodes().get(childPosition);
        }
        return render;
    }

    @AfterRenderBody
    boolean afterChild() {
        // increment the child position, afterRender on the child will have the container's currentNode set back to the node before the body was rendered.
        childPosition = childPosition + 1;
        // return true on last child index, finishing the iteration over the children, otherwise re-render the body (to render the next child)
        return node.getNavigableChildNodes().size() <= childPosition;
    }

    @AfterRender
    void after() {
        // set the currentNode to the parent after render (pop the stack)
        if (node.getParent() != null)
            node = node.getParent();
    }

    public WebNode getNode() {
        return node;
    }
}
