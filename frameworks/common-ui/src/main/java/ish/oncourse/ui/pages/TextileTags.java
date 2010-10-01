package ish.oncourse.ui.pages;

import java.util.List;

import ish.oncourse.model.Tag;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileTags {

	@Property
	private List<Tag> tags;
	@Property
	private Tag tag;
	@Property
	private Tag child;

	@Inject
	private Request request;

	private String entityType;
	private boolean showDetails;

	@SuppressWarnings("unchecked")
	void beginRender() {
		tags = (List<Tag>) request.getAttribute("textileTags");
		entityType = (String) request.getAttribute("textileTagsEntityType");
		showDetails = Boolean.TRUE.equals(request
				.getAttribute("textileTagsShowDetails"));
	}

	public boolean isHasTags() {
		return tags != null && !tags.isEmpty();
	}

	public String getTagLink() {
		return tag.getLink(entityType);
	}

	public String getChildLink() {
		return child.getLink(entityType);
	}

	public boolean isShowDetails() {
		return showDetails && child.getDetail() != null;
	}
}
