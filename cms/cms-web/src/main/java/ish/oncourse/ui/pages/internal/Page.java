package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.ui.components.internal.PageStructure;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;

public class Page {
	
	private static final String INTERNAL_PAGE_BODY_CLASS = "internal-page";

	private static final String MAIN_PAGE_BODY_CLASS = "main-page";

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
	private Block reloadPageBlock;

	
	public String getBodyId() {
		return (isHomePage() || this.node == null) ? MAIN_BODY_ID : (PAGE_BODY_ID_PREFIX + this.node.getNodeNumber());
	}
	
	@AfterRender
	void afterRender() {
		final Session session = request.getSession(false);
		session.setAttribute(WebNode.LOADED_NODE, null);
	}

	@SetupRender
	boolean setupRender() {
		
		WebNode currentNode = webNodeService.getCurrentNode();
		
		if (currentNode != null) {
			this.node = (WebNode) cayenneService.newContext().localObject(currentNode.getObjectId(), null);
		} else {
			this.node = null;
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
