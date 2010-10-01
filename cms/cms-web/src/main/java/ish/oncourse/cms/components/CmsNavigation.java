package ish.oncourse.cms.components;

import ish.oncourse.cms.pages.Login;
import ish.oncourse.cms.pages.Page;
import ish.oncourse.cms.services.security.annotations.Protected;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.services.persistence.ICayenneService;

import java.io.IOException;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

@Protected
public class CmsNavigation {

	@Inject
	private Request request;

	@InjectPage
	private Login loginPage;

	@InjectPage
	private Page newPage;

	@Inject
	private Cookies cookies;

	@Inject
	private ICayenneService cayenneService;

	public Object onActionFromCreatePage() throws IOException {
		ObjectContext ctx = cayenneService.newContext();
		WebNode page = ctx.newObject(WebNode.class);
		newPage.selectActiveNode(page);
		return newPage;
	}

	public Object onActionFromLogout() throws IOException {

		Session session = request.getSession(false);
		cookies.removeCookieValue(Login.CMS_COOKIE_NAME);

		if (session != null) {
			session.invalidate();
		}

		return loginPage;
	}
}
