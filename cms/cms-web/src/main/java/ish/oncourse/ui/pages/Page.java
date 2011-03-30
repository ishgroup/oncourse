package ish.oncourse.ui.pages;

import ish.oncourse.cms.components.PageInfo;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Page {

	@Component
	@Property
	private PageInfo pageInfo;

	@Property
	@Persist
	private WebNode node;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebNodeService webNodeService;

	@SetupRender
	public void beforeRender() {
		node = (WebNode) cayenneService.newContext().localObject(webNodeService.getCurrentNode().getObjectId(), null);
	}
	
	public String getBodyId() {
		return (isHomePage()) ? "Main" : ("page" + this.node.getId());
	}

	public String getBodyClass() {
		return (isHomePage()) ? "main-page" : "internal-page";
	}

	private boolean isHomePage() {
		return node.getId() != null && node.getId().equals(webNodeService.getHomePage().getId());
	}
}
