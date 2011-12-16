package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.ui.components.internal.PageStructure;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

public class Page {

	private static final String INTERNAL_PAGE_BODY_CLASS = "internal-page";

	private static final String MAIN_PAGE_BODY_CLASS = "main-page";

	private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";

	private static final String PAGE_PATH = "/page/";

	private static final String PAGE_BODY_ID_PREFIX = "page";

	private static final String MAIN_BODY_ID = "Main";

	@SuppressWarnings("all")
	@InjectComponent
	@Property
	private PageStructure pageStructure;

	@Property
	@Persist
	private WebNode node;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private Request request;
	@SuppressWarnings("all")
	@Inject
	private Response response;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private Block reloadPageBlock;
	
	public String getBodyId() {
		return (isHomePage() || this.node == null) ? MAIN_BODY_ID : (PAGE_BODY_ID_PREFIX + this.node.getNodeNumber());
	}

	public boolean setupRender() {
		WebNode currentNode = webNodeService.getCurrentNode();
		if (currentNode != null) {
			node = (WebNode) cayenneService.newContext().localObject(currentNode.getObjectId(), null);
		} else {
			node = null;
		}
		if (node == null) {
			WebNode newPageNode = webNodeService.createNewNode();
			newPageNode.getObjectContext().commitChanges();
			try {
				HttpServletResponse httpServletResponse = requestGlobals.getHTTPServletResponse();
				httpServletResponse.setContentType(DEFAULT_CONTENT_TYPE);
				httpServletResponse.sendRedirect("http://" + request.getServerName() + PAGE_PATH + newPageNode.getNodeNumber());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public String getBodyClass() {
		return (isHomePage()) ? MAIN_PAGE_BODY_CLASS : INTERNAL_PAGE_BODY_CLASS;
	}

	private boolean isHomePage() {
		WebNode homePage = webNodeService.getHomePage();
		if (homePage == null) {
			return false;
		}
		return node != null && node.getId() != null && node.getId().equals(homePage.getId());
	}

	public Block getReloadPageBlock() {
		return reloadPageBlock;
	}
}
