package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.visitor.LastEditedVisitor;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {

	@Parameter
	@Property
	private WebNode node;

	@Inject
	private Messages messages;

	@Property
	@Component
	private Zone inspectorZone;

	public String getPageStatus() {
		String key = "status."
				+ ((node.isPublished()) ? "published" : "nopublished");
		return messages.get(key);
	}

	public String getLastEdited() {
		return node.accept(new LastEditedVisitor(messages));
	}
}
