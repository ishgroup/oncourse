package ish.oncourse.cms.pages;

import java.io.IOException;
import java.net.URL;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;

public class Pages {
	
	@Property
	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private Request request;
	
	@Property
	private WebNode webNode;
	
	public Object onActionFromNewPage() throws IOException {
		WebNode node = webNodeService.newWebNode();
		return new URL("http://" + request.getServerName() + "/page/" + node.getNodeNumber());
	}
}
