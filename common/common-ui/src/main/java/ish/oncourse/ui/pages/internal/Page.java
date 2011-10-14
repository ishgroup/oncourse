package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Page {

	@Property
	private WebNode node;

	@Inject
	private IWebNodeService webNodeService;

	@SetupRender
	public void beforeRender() {
		node = webNodeService.getCurrentNode();
	}

	public String getBodyId() {
		return (isHomePage()) ? "Main" : ("page" + this.node.getNodeNumber());
	}

	public String getBodyClass() {
		return (isHomePage()) ? "main-page" : "internal-page";
	}

	private boolean isHomePage() {
		WebNode homePage = webNodeService.getHomePage();
		if(homePage==null){
			return false;
		}
		return node.getId() != null && node.getId().equals(homePage.getId());
	}
}
