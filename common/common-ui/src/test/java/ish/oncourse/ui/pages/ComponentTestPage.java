package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.components.CoursesListSkeleton;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentTestPage extends ISHCommon {

	private static final Logger logger = LogManager.getLogger();

	public static String CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME = "currentRenderedComponent";

	private String renderedComponentName;

	@Inject
	private Block relatedProducts, relatedCourses, coursesListSkeleton;

	public CourseItemModel getCourseItemModel() {
		return (CourseItemModel) request.getAttribute("ui_test_courseItemModel");
	}

	public Course getCourse() {
		return (Course) request.getAttribute("ui_test_course");
	}

	public List<Course> getCourses() {
		return (List<Course>) request.getAttribute("ui_test_courses");
	}

	public Long getCoursesCount() {
		return (Long) request.getAttribute("ui_test_coursesCount");
	}

	public String getSitesParameter() {
		return (String) request.getAttribute("ui_test_sitesParameter");
	}

	public SearchParams getSearchParams() {
		return (SearchParams) request.getAttribute("ui_test_searchParams");
	}

	public Set<Long> getCoursesIds() {
		return (Set<Long>) request.getAttribute("ui_test_coursesIds");
	}

	public Map getDebugInfoMap() {
		return (Map) request.getAttribute("ui_test_debugInfoMap");
	}

	public Object getRenderedComponentName() {
		switch (renderedComponentName) {
			case "ui/relatedporducts":
				return relatedProducts;
			case "ui/CourseRelations":
				return relatedCourses;
			case "ui/CoursesListSkeleton":
				return coursesListSkeleton;
			default:
				return null;
		}
	}

	@SetupRender
	public void beforeRender() {
		renderedComponentName = (String) request.getAttribute(CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME);
	}
}
