package ish.oncourse.cms.components;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.ui.pages.internal.Page;

import java.net.URL;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CmsNavigation {
	@InjectPage
	private Page page;

	@Inject
	private Request request;

	@Inject
	@Property
	private IAuthenticationService authenticationService;

	@Property
	@Inject
	private IWebMenuService webMenuService;

	@Property
	@Parameter
	private WebNode node;

	@InjectComponent
	@Property
	private PageInfo pageInfo;

	@InjectComponent
	private Pages pagesComponent;

	@SetupRender
	public void beforeRender() {

	}

	public URL onActionFromLogout() throws Exception {
		authenticationService.logout();
		Request request = this.request;
		return new URL("http://" + request.getServerName());
	}

	public Object onActionFromPages() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		return pagesComponent.getPagesListZone().getBody();
	}
}
