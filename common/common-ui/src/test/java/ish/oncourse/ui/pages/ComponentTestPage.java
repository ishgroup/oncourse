package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ComponentTestPage extends ISHCommon {

	private static final Logger logger = LogManager.getLogger();

	public static String CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME = "currentRenderedComponent";

	private String renderedComponentName;

	@Inject
	private Block relatedProducts, relatedCourses;

	public CourseItemModel getCourseItemModel() {
		return (CourseItemModel) request.getAttribute("ui_test_courseItemModel");
	}

	public Course getCourse() {
		return (Course) request.getAttribute("ui_test_course");
	}

	public Object getRenderedComponentName() {
		switch (renderedComponentName) {
			case "ui/relatedporducts":
				return relatedProducts;
			case "ui/CourseRelations":
				return relatedCourses;
			default:
				return null;
		}
	}

	@SetupRender
	public void beforeRender() {
		renderedComponentName = (String) request.getAttribute(CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME);
	}
}
