package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.ui.pages.Page;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class NewPage extends Page {
	@Property
	private String name;

	@Persist
	private WebNode page;

	@Override
	public WebNode getCurrentNode() {
		return this.page;
	}

	public void setPage(WebNode page) {
		this.page = page;
	}
}
