package ish.oncourse.ui.components;

import ish.oncourse.services.node.IWebNodeService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {
	@Property
	@Inject
	private IWebNodeService webNodeService;
}	
