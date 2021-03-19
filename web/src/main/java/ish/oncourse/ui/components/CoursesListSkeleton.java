package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.Product;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModelDaoHelper;
import ish.oncourse.ui.utils.CourseItemSkeletonModel;
import ish.oncourse.ui.utils.CourseItemSkeletonModel.CourseClassProjection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;
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
	private ISearchService searchService;

	@Property
	@Parameter(required = true)
	private List<Course> courses;

	@Property
	@Parameter(required = true)
	private SearchParams searchParams;

	@Property
	private Course course;

	@Parameter
	private Map debugInfoMap;

	@Property
	Map<String, List<CourseClassProjection>> coursesClassProjections;

	@SetupRender
	public void beforeRender() {
		Set<Long> coursesIds = courses.stream().map(Course::getId).collect(Collectors.toSet());
		Map<String, SolrDocumentList> coursesClasses = searchService.searchClasses(searchParams, coursesIds);
		coursesClassProjections = coursesClasses.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
					.stream()
					.map(this::createCourseClassProjection)
					.collect(Collectors.toList())));
	}

	private CourseClassProjection createCourseClassProjection(SolrDocument solrDocument) {
		CourseClassProjection projection = new CourseClassProjection();
		projection.setId((String) solrDocument.get("id"));
		projection.setScore((Float) solrDocument.get("score"));
		return projection;
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
		List<Course> relatedCourses = course.getRelatedCourses();

		CourseItemSkeletonModel courseItemModel = new CourseItemSkeletonModel(course, relatedProducts, relatedCourses);
		List<CourseClassProjection> projections = coursesClassProjections.get(course.getId().toString());

		if (searchParams == null || searchParams.isEmpty()) {
			courseItemModel.setAvailableClasses(projections);
		}
		else {
			Map<Boolean, List<CourseClassProjection>> groups = projections.stream().collect(Collectors.groupingBy(item -> item.getScore() > 0));
			if (groups.containsKey(true)) {
				courseItemModel.setAvailableClasses(groups.get(true));
			}
			if (groups.containsKey(false)) {
				courseItemModel.setOtherCLasses(groups.get(false));
			}
		}

		return courseItemModel;
	}
}
