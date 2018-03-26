package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoursesList extends ISHCommon {
	@Inject
	private ICourseService courseService;

	@Inject
	private ICourseClassService courseClassService;

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

	@Parameter
	private Map debugInfoMap;

    public boolean isHasMoreItems() {
        return itemIndex < coursesCount;
    }

	public Object getCourseDebugInfo() {
		if (debugInfoMap != null) {
			return debugInfoMap.get(course.getId().toString());
		} else {
			return null;
		}
	}

    public CourseItemModel getCourseItemModel() {
        return CourseItemModel.valueOf(course, searchParams);
    }

	@SuppressWarnings("unchecked")
	public String getSearchParamsStr() {
		StringBuilder result = new StringBuilder();
		result.append("start=").append(itemIndex);
		result.append("&sites=").append(sitesParameter);
		for (String paramName : request.getParameterNames()) {
			if (!"start".equals(paramName) && !"sites".equals(paramName) && !"loadedCoursesIds".equals(paramName)) {
				String[] values = request.getParameters(paramName);
				for (String value : values) {
					result.append("&");
					result.append(String.format("%s=%s", paramName, value));
				}
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
