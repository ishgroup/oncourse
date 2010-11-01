package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {

	@Parameter
	@Property
	private WebNode node;

	@Inject
	private Messages messages;

	public String getPageStatus() {
		String key = "page.status."
				+ ((node.isPublished()) ? "published"
						: "nopublished");
		return messages.get(key);
	}
}
