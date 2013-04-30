package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;
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
    private SearchParams searchParams;
    
    @Parameter
	@Property
    private List<Long> loadedCoursesIds;


    public boolean isHasMoreItems() {
        return itemIndex < coursesCount;
    }

    public CourseItemModel getCourseItemModel()
    {
        return CourseItemModel.createCourseItemModel(course, searchParams);
    }

	@SuppressWarnings("unchecked")
	public String getSearchParamsStr() {
		StringBuilder result = new StringBuilder();
		result.append("start=").append(itemIndex);
		result.append("&sites=").append(sitesParameter);
		for (String paramName : request.getParameterNames()) {
			if (!"start".equals(paramName) && !"sites".equals(paramName) && !"loadedCoursesIds".equals(paramName)) {
				result.append("&");
				result.append(paramName).append("=");
				result.append(request.getParameter(paramName));
			}
		}
		if (loadedCoursesIds == null) {
			loadedCoursesIds = Collections.EMPTY_LIST;
		} else {
			result.append("&loadedCoursesIds=");
		}
		boolean isFirstId = true;
		for (Long loadedCourseId : loadedCoursesIds) {
			if (!isFirstId) {
				result.append(",");
			}
			result.append(loadedCourseId);
			if (isFirstId) {
				isFirstId = false;
			}
		}
		return result.toString();
	}
}
