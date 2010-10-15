/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.ui.pages.Page;

public class Index extends Page {
	
	@Inject
	private IWebNodeService webNodeService;
	
	@Property
	private String templateId;
	
	@Override
	public void beforeRender() {
		super.beforeRender();
		setCurrentNode(webNodeService.getHomePage());
	}
	
}
