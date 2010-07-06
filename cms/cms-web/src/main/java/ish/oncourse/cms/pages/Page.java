package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.components.WebNodeTemplate;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Page {
	@Inject
	private IWebNodeService webNodeService;

	public IWebNodeService getWebNodeService() {
		return webNodeService;
	}

	@Property
	@Component(id = "webNodeTemplate", parameters = { "node=currentNode"})
	private WebNodeTemplate webNodeTemplate;

	public WebNode getCurrentNode() {
		return webNodeService.getCurrentPage();
	}
}
