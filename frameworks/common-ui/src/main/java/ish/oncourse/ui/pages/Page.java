package ish.oncourse.ui.pages;

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
		return node.getId() != null && node.getId().equals(webNodeService.getHomePage().getId());
	}
}
