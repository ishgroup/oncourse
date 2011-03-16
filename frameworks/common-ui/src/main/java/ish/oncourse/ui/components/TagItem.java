package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.TextileUtil;

import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TagItem {
	@Parameter(required = true, cache = false)
	private Tag tag;

	@Parameter
	private int childPosition;

	@Parameter
	private Integer maxLevels;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	private boolean showDetails;

	private Integer currentDepth = 0;

	@SetupRender
	boolean beginRender() {
		showDetails = Boolean.TRUE.equals(request
				.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_DETAILS_PARAM));
		// prevents rending with the menu parameter is null or it can't be
		// rendered
		final boolean render = tag != null && (maxLevels == null || maxLevels > 0);
		return render;
	}

	@BeforeRenderBody
	boolean beforeChild() {
		List<Tag> webVisibleTags = tag.getWebVisibleTags();
		// if the tag has children, render the body to render a child
		currentDepth++;

		final boolean render = webVisibleTags.size() > 0
				&& (maxLevels == null || maxLevels > currentDepth);
		if (render) {
			// sets the container's currentTag to the tag's child at the given
			// index.
			tag = webVisibleTags.get(childPosition);
		}
		return render;
	}

	@AfterRenderBody
	boolean afterChild() {
		// increment the child position, afterRender on the child will have the
		// container's currentTag set back to the menu before the body was
		// rendered.
		childPosition = childPosition + 1;
		currentDepth--;
		// return true on last child index, finishing the iteration over the
		// children, otherwise re-render the body (to render the next child)
		return tag.getWebVisibleTags().size() <= childPosition;
	}

	@AfterRender
	void after() {

		// set the currentTag to the parent after render (pop the stack)
		Tag parentTag = tag.getParent();
		if (parentTag != null) {
			tag = parentTag;
		}
	}

	public Tag getTag() {
		return tag;
	}

	public boolean isShowDetails() {
		return showDetails && tag.getDetail() != null;
	}

	public String getTagItemClass() {
		StringBuffer result = new StringBuffer();
		if ((!tag.getWebVisibleTags().isEmpty())
				&& (maxLevels == null || maxLevels > currentDepth + 1)) {
			result.append(messages.get("li.class.hasChildren"));
		}
		String tagLink = getTagLink().toLowerCase();
		String requestPath = request.getPath().toLowerCase();
		String searchTagParameter = request.getParameter("subject") == null ? "" : request
				.getParameter("subject").toLowerCase();
		String defaultPath = tag.getDefaultPath().toLowerCase();
		if (requestPath.endsWith(tagLink) || defaultPath.equals(searchTagParameter)) {
			result.append(" ").append(messages.get("li.class.selected"));
		} else if (requestPath.contains(tagLink) || searchTagParameter.contains(defaultPath)) {
			result.append(" ").append(messages.get("li.class.childSelected"));
		}
		return result.toString();
	}

	public String getTagLink() {
		return tag.getLink();
	}
}
