package ish.oncourse.model;

import java.util.Comparator;

public class WebContentComparator implements Comparator<WebContent>{

	private WebNodeType webNodeType;
	
	public WebContentComparator(WebNodeType webNodeType) {
		this.webNodeType = webNodeType;
	}
	
	public int compare(WebContent o1, WebContent o2) {
		WebContentVisibility visibility1 = o1.getWebContentVisibility(webNodeType);
		WebContentVisibility visibility2 = o2.getWebContentVisibility(webNodeType);
		if(visibility1==null||visibility2==null){
			return o1.compareTo(o2);
		}
		return visibility1.compareTo(visibility2);
	}

}
