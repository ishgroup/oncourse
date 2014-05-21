package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Page {

	@Property
	private WebNode node;
	
	@Inject
	private Request request;
	
	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IFacebookMetaProvider facebookMetaProvider;
	
	private static final Logger logger = Logger.getLogger(Page.class);

	@SetupRender
	public boolean beforeRender() throws IOException {
		node = webNodeService.getCurrentNode();
		if (node == null || !node.isPublished()) {
			logger.warn(String.format("CurrentNode \"%s\" is %s in %s/%s",
                    node == null ? "undefined": webNodeService.getPath(node),
                    node == null ? "null" : "unpublished",
                    request.getServerName(),
                    request.getPath()));
			
			HttpServletResponse httpServletResponse = requestGlobals.getHTTPServletResponse();
			httpServletResponse.setContentType("text/html;charset=UTF-8");
			httpServletResponse.sendRedirect("http://" + request.getServerName() + "/PageNotFound");
		}
		return true;
	}
	
	public String getBodyId() {
		return (isHomePage() || this.node == null) ? "Main" : ("page" + this.node.getNodeNumber());
	}

    public WebNodeType getWebNodeType()
    {
        return node != null ? node.getWebNodeType() : null;
    }

	public String getBodyClass() {
		return (isHomePage()) ? "main-page" : "internal-page";
	}

	private boolean isHomePage() {
		WebNode homePage = webNodeService.getHomePage();
		if(homePage==null){
			return false;
		}
		return node != null && node.getId() != null && node.getId().equals(homePage.getId());
	}


	public String getMetaDescription() {
		return facebookMetaProvider.getDescriptionContent(node);
	}
}
