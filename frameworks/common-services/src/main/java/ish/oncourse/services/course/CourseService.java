package ish.oncourse.services.course;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParam;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseService implements ICourseService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	@Inject
	private Request request;

	@Inject
	ISearchService searchService;

	public List<Course> getCourses(Integer startDefault, Integer rowsDefault) {
		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(getSiteQualifier());
		if (startDefault == null) {
			startDefault = START_DEFAULT;
		}
		if (rowsDefault == null) {
			rowsDefault = ROWS_DEFAULT;
		}
		q.setFetchOffset(startDefault);
		q.setFetchLimit(rowsDefault);
		
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY+"."+CourseClass.ENROLMENTS_PROPERTY);
		
		return cayenneService.sharedContext().performQuery(q);
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()).andExp(
				getAvailabilityQualifier());
	}

	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true);
	}

	public List<Course> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(ExpressionFactory.inDbExp("id", ids));

		return cayenneService.sharedContext().performQuery(q);
	}

	@SuppressWarnings("unchecked")
	public Course getCourse(String searchProperty, Object value) {
		College currentCollege = webSiteService.getCurrentCollege();
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = ExpressionFactory.matchExp(
				BinaryInfo.COLLEGE_PROPERTY, currentCollege);
		if (searchProperty != null) {
			if (searchProperty.equals(Course.CODE_PROPERTY)) {
				qualifier = qualifier.andExp(getSearchStringPropertyQualifier(
						searchProperty, value));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(
						searchProperty, value));
			}
		}
		SelectQuery q = new SelectQuery(Course.class, qualifier);
		List<Course> result = sharedContext.performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	public Expression getSearchStringPropertyQualifier(String searchProperty,
			Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public Course getCourse(Boolean enrollable, String taggedWith,
			Boolean currentSearch) {
		List<Course> result = new ArrayList<Course>();

		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(getSiteQualifier());
		if (taggedWith != null) {
			q.andQualifier(ExpressionFactory.inExp(
					"db:ID",
					tagService.getEntityIdsByTagName(taggedWith,
							Course.class.getSimpleName())));
		}

		if (currentSearch != null) {

			Map<SearchParam, String> courseSearchParams = getCourseSearchParams();
			if (!courseSearchParams.isEmpty()) {

				QueryResponse resp = searchService.searchCourses(
						courseSearchParams, 0, 100);

				List<String> ids = new ArrayList<String>(resp.getResults()
						.size());

				for (SolrDocument doc : resp.getResults()) {
					ids.add((String) doc.getFieldValue("id"));
				}
				if (currentSearch) {
					q.andQualifier(ExpressionFactory.inExp("db:ID", ids));
				} else {
					q.andQualifier(ExpressionFactory.notInExp("db:ID", ids));
				}
			}
		}
		List<Course> courses = cayenneService.sharedContext().performQuery(q);
		if (enrollable == null) {
			result.addAll(courses);
		} else {
			if (enrollable) {
				for (Course course : courses) {
					if (!course.getEnrollableClasses().isEmpty()) {
						result.add(course);
					}
				}
			} else {
				for (Course course : courses) {
					if (course.getEnrollableClasses().isEmpty()) {
						result.add(course);
					}
				}
			}
		}

		return result.get(new Random().nextInt(result.size()));
	}

	public Integer getCoursesCount() {
		return ((Number) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery("select count(c) from Course c where "
								+ getSiteQualifier().toEJBQL("c"))).get(0))
				.intValue();
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery(
								"select max(c.modified) from Course c where "
										+ getSiteQualifier().toEJBQL("c")))
				.get(0);
	}

	public Map<SearchParam, String> getCourseSearchParams() {
		Map<SearchParam, String> searchParams = new HashMap<SearchParam, String>();

		for (SearchParam name : SearchParam.values()) {
			String parameter = request.getParameter(name.name());
			if (parameter != null && !"".equals(parameter)) {
				searchParams.put(name, parameter);
			}
		}
		Tag browseTag = null;
		if (searchParams.containsKey(SearchParam.subject)) {
			String path = searchParams.get(SearchParam.subject);
			browseTag = tagService.getSubTagByName(path.substring(path
					.lastIndexOf("/") + 1));
		} else {
			browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
			if (browseTag != null) {
				searchParams.put(SearchParam.subject, browseTag.getName());
			}
		}
		request.setAttribute("browseTag", browseTag);

		return searchParams;
	}
}
