package ish.oncourse.cms.pages;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BlockEdit {
	
	@Property
	private WebContent block;
	
	@Inject
	private IWebContentService webContentService;
	
	public void onActivate(Integer id) {
		this.block = webContentService.loadByIds(id).get(0);
	}
}
