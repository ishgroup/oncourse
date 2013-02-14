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
		WebNode node = null; 
		if (node == null) {
			final Session session = request.getSession(false);
			if (session != null) {
				node = (WebNode) session.getAttribute(WebNode.LOADED_NODE);
				if (node == null) {
					node = super.getCurrentNode();
				}
			}
		}
		return node;
	}
	
}
