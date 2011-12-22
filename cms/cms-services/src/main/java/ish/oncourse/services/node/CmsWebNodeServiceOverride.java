package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class CmsWebNodeServiceOverride extends WebNodeService {
	
	@Inject
	private Request request;
	
	@Override
	public WebNode getCurrentNode() {
		WebNode node = super.getCurrentNode();
		final Session session = request.getSession(false);
		if (session != null) {
			if (node != null) {
				session.setAttribute(WebNode.LOADED_NODE, node);
			}
			else {
				node = (WebNode) session.getAttribute(WebNode.LOADED_NODE);
			}
		}
		return node;
	}
	
}
