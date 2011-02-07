package ish.oncourse.ui.pages;

import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.ui.components.TagItem;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileTags {
	@Property
	private Tag rootTag;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	private String entityType;

	@Property
	@Component(id = "childTag", parameters = { "tag=currentTag",
			"childPosition=currentChildPosition" })
	private TagItem childTag;

	private Tag currentTag;

	private Map<Tag, Integer> childPositions;

	@SetupRender
	void beginRender() {
		childPositions = new HashMap<Tag, Integer>();
		rootTag = (Tag) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM);
		entityType = (String) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_ENTITY_PARAM);

	}

	public Tag getCurrentTag() {
		return currentTag;
	}

	public void setCurrentTag(final Tag tag) {
		if (!childPositions.containsKey(tag)) {
			childPositions.put(tag, 0);
		}
		currentTag = tag;
	}

	public int getCurrentChildPosition() {
		return childPositions.get(currentTag);
	}

	public void setCurrentChildPosition(final int pos) {
		this.childPositions.put(currentTag, pos);
	}

	public String getRootTagClass() {
		StringBuffer result = new StringBuffer();
		if (!rootTag.getWebVisibleTags().isEmpty()) {
			result.append(messages.get("li.class.hasChildren"));
		}
		String rootTagLink = getRootTagLink();
		String requestPath = request.getPath();
		String searchTagParameter = request.getParameter("subject");
		String defaultPath = rootTag.getDefaultPath();
		if (requestPath.endsWith(rootTagLink) || defaultPath.equals(searchTagParameter)) {
			result.append(" ").append(messages.get("li.class.selected"));
		} else if (requestPath.contains(rootTagLink)
				|| (searchTagParameter != null && searchTagParameter.contains(defaultPath))) {
			result.append(" ").append(messages.get("li.class.childSelected"));
		}
		return result.toString();
	}

	public String getRootTagLink() {
		return rootTag.getLink(entityType);
	}

}
