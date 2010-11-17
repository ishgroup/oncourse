package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.visitor.LastEditedVisitor;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageTypes {

	@Inject
	@Property
	private IWebNodeService webNodeService;

	@Inject
	private Messages messages;

	@Property
	private WebNodeType webNodeType;

	public String getLastEdited() {
		return webNodeType.accept(new LastEditedVisitor(messages));
	}
}
