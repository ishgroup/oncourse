package ish.oncourse.textile.pages;

import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.renderer.tags.ActiveTag;
import ish.oncourse.services.textile.renderer.tags.GetActiveTags;
import ish.oncourse.textile.components.TagItem;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.SPACE;

public class TextileTags {
	@Property
	private Tag rootTag;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Inject
	private ITagService tagService;

	@Inject
	private ISearchService searchService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICourseService courseService;

	private String entityType;

	@SuppressWarnings("all")
	@Property
	@Component(id = "childTag", parameters = { "tag=currentTag",
			"childPosition=currentChildPosition", "maxLevels=maxLevelsForChild", "counters=counters", "activeTags=activeTags" })
	private TagItem childTag;

	private Tag currentTag;

	private Map<Tag, Integer> childPositions;

	private Integer maxLevels;
	@SuppressWarnings("all")
	@Property
	private boolean showTopLevel;
	@SuppressWarnings("all")
	@Property
	private Integer maxLevelsForChild;

	@Property
	private Map<Long, Long> counters;

	@Property
	private List<Tag> activeTags;

	@SetupRender
	boolean beginRender() {
		childPositions = new HashMap<>();
		rootTag = (Tag) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM);
		maxLevels = (Integer) request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_MAX_LEVEL_PARAM);
		
		if (maxLevels != null) {
			maxLevelsForChild = maxLevels;
		}
		
		showTopLevel = !(Boolean) request
				.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_HIDE_TOP_PARAM);

		if (request.getAttribute(TextileUtil.TEXTILE_TAGS_PAGE_MULTISELECT_TAGS_PARAM) != null) {
			SearchParamsParser parser = SearchParamsParser.valueOf(request,
					webSiteService.getCurrentCollege(),
					searchService,
					tagService,
					webSiteService.getTimezone());
			parser.parse();
			counters = searchService.getCountersForTags(parser.getSearchParams(), rootTag.getAllWebVisibleChildren());
		} else {
			counters = Collections.emptyMap();
		}

		activeTags = GetActiveTags.valueOf(request, courseService, tagService).get();

		return rootTag != null && (maxLevels == null || maxLevels > 0);
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
		StringBuilder result = new StringBuilder();
		if (!rootTag.getWebVisibleTags().isEmpty()) {
			result.append(messages.get("li.class.hasChildren"));
		}

		ActiveTag activeTag = ActiveTag.valueOf(rootTag, activeTags);
		if (activeTag.isActive()) {
			result.append(SPACE).append(messages.get("li.class.childSelected"));
		} else if (activeTag.isSelected()) {
			result.append(SPACE).append(messages.get("li.class.selected"));
		}
		return result.toString();
	}

	public String getRootTagLink() {
		return rootTag.getLink(entityType);
	}

}
