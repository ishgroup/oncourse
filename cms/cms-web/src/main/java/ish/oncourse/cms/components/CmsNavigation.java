package ish.oncourse.cms.components;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.Protected;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;

import java.io.IOException;
import java.net.URL;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@Protected
public class CmsNavigation {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IAuthenticationService authenticationService;
	
	@Inject
	private Request request;
	
	@Property
	@Persist
	private WebNode node;
	
	@SetupRender
	public void beforeRender() {
		this.node = webNodeService.getCurrentNode();
	}

	public boolean isHasCurrentNode() {
		return this.node != null;
	}

	public Object onActionFromLogout() throws IOException {
		authenticationService.logout();
		return null;
	}
	
	public Object onActionFromNewPage() throws IOException {
		WebNode node = webNodeService.newWebNode();
		return new URL("http://" + request.getServerName() + "/page/" + node.getNodeNumber());
	}
}
