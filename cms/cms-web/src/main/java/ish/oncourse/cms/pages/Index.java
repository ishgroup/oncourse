package ish.oncourse.cms.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;

/**
 * CMS start page.
 */
public class Index {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebNodeService webNodeService;

	@Property
	private WebSite site;

	@Property
	private WebNode node;

	@Property
	private int nodeIndex;

//	@Inject
//	private Block siteMap;

//	@Inject
//	private Block pages;

	public List<WebSite> getSites() {
		return webSiteService.getAvailableSites();
	}

	public List<WebNode> getNodes() {
		return webNodeService.getNodes();
	}

	public String getNodeClass() {
		return nodeIndex % 2 == 0 ? "row_even" : "row_odd";
	}

	public String getNodeParentLink() {
		WebNode parent = node.getParentNode();
		return parent != null ? "/page/" + node.getParentNode().getNodeNumber()
				: null;
	}

	public boolean isNodeHasChildren() {
		return node.getWebNodes().size() > 0;
	}

	public String getNodeChildrenLabel() {
		int count = node.getWebNodes().size();
		return count > 1 ? count + " children" : count + " child";
	}

	public String getNodeStatusStyle() {
		return "node-" + getNodeStatus().name();
	}

	public WebNodeStatus getNodeStatus() {
		// TODO: make WebNode return this?
		if (node.getIsWebVisible() != null && node.getIsWebVisible()) {
			return node.getIsPublished() != null && node.getIsPublished() ? WebNodeStatus.published
					: WebNodeStatus.unpublished;
		} else {
			return WebNodeStatus.deleted;
		}
	}

	public boolean isSiteSelected() {
		return webSiteService.getCurrentWebSite() != null;
	}

	public void onActionFromAddPage() {
		// TODO..
	}
}
