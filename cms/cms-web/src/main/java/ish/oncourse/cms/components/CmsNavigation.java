package ish.oncourse.cms.components;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.Protected;
import ish.oncourse.model.WebNode;

import java.net.URL;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@Protected
public class CmsNavigation {

	@Inject
	@Property
	private IAuthenticationService authenticationService;

	@Inject
	private Request request;

	@Property
	@Parameter
	private WebNode node;

	@InjectComponent
	@Property
	private PageInfo pageInfo;

	@SetupRender
	public void beforeRender() {

	}

	public URL onActionFromLogout() throws Exception {
		authenticationService.logout();
		Request request = this.request;
		return new URL("http://" + request.getServerName());
	}

}
