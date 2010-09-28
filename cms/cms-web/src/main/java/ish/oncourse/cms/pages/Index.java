package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.pages.Page;

import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * CMS start page.
 */
public class Index extends Page {
	@Inject
	private IWebNodeService webNodeService;

	@Override
	public WebNode getCurrentNode() {
		return webNodeService.getHomePage();
	}
}
