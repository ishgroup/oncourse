package ish.oncourse.services.textile.renderer.tags;

import ish.oncourse.linktransform.PageIdentifier;
import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetActiveTags {
	private ICourseService courseService;
	private ITagService tagService;
	private Request request;
	private String path;

	private List<Tag> tags = new ArrayList<>();

	public List<Tag> get() {
		PageIdentifier pageIdentifier = PageIdentifier.getPageIdentifierByPath(path);
		switch (pageIdentifier) {
			case Course:
				initTagsForCourse();
				break;
			case Courses:
				initTagsForCourses();
				break;
			default:
				break;
		}
		return tags;
	}

	private void initTagsForCourses() {

		Tag tag = (Tag) request.getAttribute(PageLinkTransformer.ATTR_coursesTag);

		if (tag != null) {
			tags.add(tag);
		} else {
			String subject = request.getParameter("subject");
			if (subject != null) {
				tag = tagService.getTagByFullPath(subject);
				if (tag != null) {
					tags.add(tag);
				}
			}
		}
	}

	private void initTagsForCourse() {
		String courseCode = path.substring(path.lastIndexOf("/") + 1);
		Course course = courseService.getCourseByCode(courseCode);
		if (course != null) {
			tags.addAll(tagService.getTagsForEntity(Course.class.getSimpleName(), course.getId()));
		}
	}


	public static GetActiveTags valueOf(Request request, ICourseService courseService, ITagService tagService) {
		GetActiveTags result = new GetActiveTags();
		result.request = request;
		result.path = request.getPath();
		result.courseService = courseService;
		result.tagService = tagService;
		return result;
	}
}
