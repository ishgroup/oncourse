package ish.oncourse.ui.pages.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import ish.oncourse.linktransform.PageIdentifier;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.node.IWebNodeService;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

public class Page {

	@Property
	private WebNode node;
	
	@Inject
	private Request request;
	
	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private IWebNodeService webNodeService;
	
	private static final Logger logger = Logger.getLogger(Page.class);

	@SetupRender
	public boolean beforeRender() throws IOException {
		node = webNodeService.getCurrentNode();
		if (node == null) {
			logger.error("CurrentNode is null in " + request.getServerName());
			
			HttpServletResponse httpServletResponse = requestGlobals.getHTTPServletResponse();
			httpServletResponse.setContentType("text/html;charset=UTF-8");
			httpServletResponse.sendRedirect("http://" + request.getServerName() + "/PageNotFound");
		}
		return true;
	}
	
	public String getBodyId() {
		return (isHomePage() || this.node == null) ? "Main" : ("page" + this.node.getNodeNumber());
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
}
