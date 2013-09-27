package ish.oncourse.services.textile.courseList;

import ish.oncourse.model.Tag;
import ish.oncourse.services.course.Sort;
import ish.oncourse.services.tag.ITagService;

import java.util.Map;

public class TagParams {

	private Map<String, String> tagParams;
	private ITagService tagService;
	private Integer limit;
	private Sort sort;
	private boolean isAscending;
	private Style style;
	private boolean showTags;
	private Tag tag;

	public ITagService getTagService() {
		return tagService;
	}

	public void setTagService(ITagService tagService) {
		this.tagService = tagService;
	}

	public Map<String, String> getTagParams() {
		return tagParams;
	}

	public void setTagParams(Map<String, String> tagParams) {
		this.tagParams = tagParams;
	}

	public void parse() {
		parseLimit();
		parseSort();
		parseOrder();
		parseTag();
		parseStyle();
		parseShowTags();
	}

	private void parseShowTags() {
		String showTags = tagParams.get(TextileAttrs.showTags.getValue());
		this.showTags = showTags == null ? Boolean.FALSE : Boolean.valueOf(showTags);
	}

	private void parseStyle() {
		String style = tagParams.get(TextileAttrs.style
				.getValue());
		this.style = style != null ? Style.valueOf(style) : Style.details;
	}

	private void parseTag() {
		String tagName = tagParams.get(TextileAttrs.tag
				.getValue());
		tag = tagService.getTagByFullPath(tagName);
	}

	private void parseOrder() {
		String order = tagParams.get(TextileAttrs.order
				.getValue());
		isAscending = order == null || "asc".equalsIgnoreCase(order);
	}

	private void parseSort() {
		String sort = tagParams.get(TextileAttrs.sort
				.getValue());
		try {
			this.sort = Sort.valueOf(sort);
		} catch (Exception e) {
			this.sort = Sort.alphabetical;
		}
	}

	private void parseLimit() {
		String limit = tagParams.get(TextileAttrs.limit
				.getValue());
		this.limit = limit == null ? null : Integer.valueOf(limit);
	}

	public Sort getSort() {
		return sort;
	}

	public Integer getLimit() {
		return limit;
	}

	public boolean isAscending() {
		return isAscending;
	}

	public Style getStyle() {
		return style;
	}

	public boolean isShowTags() {
		return showTags;
	}

	public Tag getTag() {
		return tag;
	}
}
