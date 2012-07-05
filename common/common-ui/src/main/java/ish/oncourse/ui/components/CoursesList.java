package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.ui.utils.CourseItemModel;
import ish.oncourse.ui.utils.Suburb;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class CoursesList {
	@Inject
	@Property
	private Request request;

	@Parameter
	private Integer coursesCount;

	@Parameter
	private Integer itemIndex;

	@SuppressWarnings("all")
	@Parameter
	@Property
	private List<Course> courses;

	@Parameter
	@Property
	private String sitesParameter;

	@SuppressWarnings("all")
	@Property
	private Course course;

    @Property
    @Parameter
    private Suburb suburb;


    public boolean isHasMoreItems() {
        return itemIndex < coursesCount;
    }

    public CourseItemModel getCourseItemModel()
    {
        return CourseItemModel.createCourseItemModel(course, suburb);
    }

	public String getSearchParamsStr() {
		StringBuffer result = new StringBuffer();
		result.append("start=").append(itemIndex);
		result.append("&sites=").append(sitesParameter);
		for (String paramName : request.getParameterNames()) {
			if (!paramName.equals("start") && !paramName.equals("sites")) {
				result.append("&");
				result.append(paramName).append("=");
				result.append(request.getParameter(paramName));
			}
		}

		return result.toString();
	}
}
