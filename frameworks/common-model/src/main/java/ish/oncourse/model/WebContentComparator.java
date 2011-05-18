package ish.oncourse.model;

import java.util.Comparator;

public class WebContentComparator implements Comparator<WebContent> {

	private WebNodeType webNodeType;

	private WebNode webNode;

	public WebContentComparator(WebNodeType webNodeType) {
		this.webNodeType = webNodeType;
	}

	public WebContentComparator(WebNode webNode) {
		this.webNode = webNode;
	}

	public int compare(WebContent o1, WebContent o2) {
		WebContentVisibility visibility1 = o1.getWebContentVisibility(webNode, webNodeType);
		WebContentVisibility visibility2 = o2.getWebContentVisibility(webNode, webNodeType);
		if (visibility1 == null || visibility2 == null) {
			return o1.compareTo(o2);
		}
		return visibility1.compareTo(visibility2);
	}

}
