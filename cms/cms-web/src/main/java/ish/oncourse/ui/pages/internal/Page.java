package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.ui.components.internal.PageStructure;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.ActionLink;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

public class Page {

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
	@Inject
	private Response response;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private Block reloadPageBlock;
	
	public String getBodyId() {
		return (isHomePage() || this.node == null) ? "Main" : ("page" + this.node.getNodeNumber());
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
				httpServletResponse.setContentType("text/html;charset=UTF-8");
				httpServletResponse.sendRedirect("http://" + request.getServerName() + "/page/"
						+ newPageNode.getNodeNumber());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public String getBodyClass() {
		return (isHomePage()) ? "main-page" : "internal-page";
	}

	private boolean isHomePage() {
		WebNode homePage = webNodeService.getHomePage();
		if (homePage == null) {
			return false;
		}
		return node.getId() != null && node.getId().equals(homePage.getId());
	}

	public Block getReloadPageBlock() {
		return reloadPageBlock;
	}
}
