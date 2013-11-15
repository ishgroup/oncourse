package ish.oncourse.services.textile.courseList;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class provides all nessesarry to render CourseList page.
 */
public class PageModel {

	private TagParams tagParams;
	private ITagService tagService;
	private ICourseService courseService;

	private ISearchService searchService;

	//can be null
	private Tag tag;

	private List<Tag> childTags;
	private Map<Tag, List<Course>> courses;
	private Style style;
	private boolean showTags;

	public void init()
	{
		style = tagParams.getStyle();
		showTags = tagParams.isShowTags();

		initTags();
		initCourses();
	}

	private void initCourses() {
		courses = new HashMap<>();

		if (showTags)
		{
			int limit = childTags.size();
			//limit number for childrent
			if (tagParams.getLimit() != null && limit > tagParams.getLimit())
				limit = tagParams.getLimit();

			for (int i = 0; i < limit; i++) {
				Tag tag = childTags.get(i);
				courses.put(tag, courseService.getCourses(tag, tagParams.getSort(), tagParams.isAscending(), tagParams.getLimit()));
			}
		}
		else
		{
			courses.put(tag,
					courseService.getCourses(
							//when tag paremeter in this rich-tag is not defiend we need load all courses.
							tag.getId().equals(tagService.getSubjectsTag().getId()) ? null: tag,
							tagParams.getSort(),
							tagParams.isAscending(),
							tagParams.getLimit()));
		}

	}

	private void initTags() {
		this.childTags = new ArrayList<>();
		this.tag = tagParams.getTag();
		if (this.tag == null)
			this.tag = tagService.getSubjectsTag();
		if (showTags)
			this.childTags.addAll(this.tag.getWebVisibleTags());
	}

	public TagParams getTagParams() {
		return tagParams;
	}

	public void setTagParams(TagParams tagParams) {
		this.tagParams = tagParams;
	}

	public ITagService getTagService() {
		return tagService;
	}

	public void setTagService(ITagService tagService) {
		this.tagService = tagService;
	}

	public ICourseService getCourseService() {
		return courseService;
	}

	public void setCourseService(ICourseService courseService) {
		this.courseService = courseService;
	}

	public Tag getTag() {
		return tag;
	}

	public List<Tag> getChildTags() {
		return childTags;
	}

	public Style getStyle() {
		return style;
	}

	public boolean isShowTags() {
		return showTags;
	}

	public List<Course> getCoursesBy(Tag tag) {
		return courses.get(tag);
	}

	public boolean isEmpty()
	{
		return courses.isEmpty();
	}

	public ISearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(ISearchService searchService) {
		this.searchService = searchService;
	}
}
