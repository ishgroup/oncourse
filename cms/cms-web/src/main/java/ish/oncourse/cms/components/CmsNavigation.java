package ish.oncourse.cms.components;

import ish.oncourse.cms.pages.Login;
import ish.oncourse.cms.pages.Page;
import ish.oncourse.cms.services.security.annotations.Protected;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

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
	
	@Inject
	private IWebSiteService webSiteService;

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
		
		WebSite webSite = webSiteService.getCurrentWebSite();
		
		page.setWebSite((WebSite) ctx.localObject(webSite.getObjectId(), null));
		
		page.setWeighting(0);
		
		page.setNodeNumber(0);
		
		page.setWebNodeType(WebNodeType
				.forName(ctx, WebNodeType.PAGE));
		
		WebNodeContent content = ctx.newObject(WebNodeContent.class);
		content.setRegionKey("content");
		content.setContent("Sample content text.");
		page.addToWebNodeContents(content);
		
		newPage.selectNode(page);
		
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
