package ish.oncourse.ui.pages;

import java.util.List;

import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.TextileUtil;

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
		tags = (List<Tag>) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_TAGS_PARAM);
		entityType = (String) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_ENTITY_PARAM);
		showDetails = Boolean.TRUE.equals(request
				.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_DETAILS_PARAM));
	}

	public boolean isHasTags() {
		return tags != null && !tags.isEmpty();
	}

	public String getTagLink() {
		return request.getContextPath() + tag.getLink(entityType);
	}

	public String getChildLink() {
		return request.getContextPath() + child.getLink(entityType);
	}

	public boolean isShowDetails() {
		return showDetails && child.getDetail() != null;
	}
}
