package ish.oncourse.website.pages;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.components.WebNodeTemplate;


public class Page {

	private static final Logger LOGGER = Logger.getLogger(Page.class);

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private Request request;


	public IWebNodeService getWebNodeService() {
		return webNodeService;
	}

	@Property
	@Component(id = "webNodeTemplate", parameters = { "node=currentNode"})
	private WebNodeTemplate webNodeTemplate;


	public WebNode getCurrentNode() {

		WebNode node = null;

		if (request.getAttribute(IWebNodeService.NODE) != null) {
			return (WebNode) request.getAttribute(IWebNodeService.NODE);
		} else if (request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			try {
				Integer nodeNumber = Integer.parseInt(request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
				node = webNodeService.getNodeForNodeNumber(nodeNumber);
			} catch(Exception e) {
				LOGGER.debug("Unable to convert node number to integer: "
						+ request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
			}
		} else if (request.getAttribute(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			try {
				Integer nodeNumber = Integer.parseInt(request.getAttribute(IWebNodeService.NODE_NUMBER_PARAMETER).toString());
				node = webNodeService.getNodeForNodeNumber(nodeNumber);
			} catch(Exception e) {
				LOGGER.debug("Unable to convert node number to integer: "
						+ request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
			}
		} else if (request.getParameter(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			String pagePath = request.getParameter(IWebNodeService.PAGE_PATH_PARAMETER);
			node = webNodeService.getNodeForNodeName(pagePath);
		} 

		return node;
	}
}
