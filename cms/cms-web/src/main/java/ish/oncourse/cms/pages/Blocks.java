package ish.oncourse.cms.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;

public class Blocks {
	
	@Property
	@Inject
	private IWebContentService webContentService;
	
	@Property
	private WebContent block;
}
