package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModelDaoHelper;
import ish.oncourse.ui.utils.CourseItemSkeletonModel;
import ish.oncourse.ui.utils.CourseItemSkeletonModel.CourseClassProjection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CoursesListSkeleton extends ISHCommon {

	private static Logger logger = LogManager.getLogger();

	@Inject
	private ICourseService courseService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private ISearchService searchService;

	@Parameter
	private Integer coursesCount;

	/*
	@Parameter
	private Integer itemIndex;

    @Parameter
	@Property
    private List<Long> loadedCoursesIds;

	public boolean isHasMoreItems() {
        return itemIndex < coursesCount;
    }
	*/

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
	private Map debugInfoMap;

	@Parameter
	@Property
	private Set<Long> coursesIds;

	private Map<String, SolrDocumentList> coursesClasses;

	@Property
	Map<String, List<CourseClassProjection>> coursesClassProjections;

	@SetupRender
	public void beforeRender() {
		coursesClasses = searchService.searchClasses(searchParams, coursesIds);

		coursesClassProjections = coursesClasses.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue()
					.stream()
					.map(solrDocument -> {
						CourseClassProjection projection = new CourseClassProjection();
						projection.setId((String) solrDocument.get("id"));
						projection.setScore("");
						return projection;
					})
					.collect(Collectors.toList())));
	}

	public Object getCourseDebugInfo() {
		if (debugInfoMap != null) {
			return debugInfoMap.get(course.getId().toString());
		} else {
			return null;
		}
	}

	public CourseItemSkeletonModel getCourseItemModel() {
		List<Product> relatedProducts = CourseItemModelDaoHelper.selectRelatedProducts(course);
		List<Course> relatedCourses = CourseItemModelDaoHelper.selectRelatedCourses(course);

		CourseItemSkeletonModel courseItemModel = new CourseItemSkeletonModel(course, relatedProducts, relatedCourses);
		courseItemModel.setAvailableClasses(coursesClassProjections.get(course.getId().toString()));
		return courseItemModel;
		//return CourseItemModel.valueOf(course, searchParams);
	}

	@SuppressWarnings("unchecked")
	public String getSearchParamsStr() {
		StringBuilder result = new StringBuilder();
		//result.append("start=").append(itemIndex);
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
/*		if (loadedCoursesIds == null) {
			loadedCoursesIds = Collections.EMPTY_LIST;
		} else {
			result.append("&loadedCoursesIds=");
		}*/
		boolean isFirstId = true;
/*		for (Long loadedCourseId : loadedCoursesIds) {
			if (!isFirstId) {
				result.append(",");
			}
			result.append(loadedCourseId);
			if (isFirstId) {
				isFirstId = false;
			}
		}*/
		return result.toString();
	}
}
