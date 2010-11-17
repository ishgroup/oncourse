package ish.oncourse.cms.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.visitor.LastEditedVisitor;

public class Blocks {

	@Property
	@Inject
	private IWebContentService webContentService;

	@Inject
	private Messages messages;

	@Property
	private WebContent block;

	public String getLastEdited() {
		return block.accept(new LastEditedVisitor(messages));
	}
}
