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
import ish.oncourse.services.textile.attrs.CourseListSortValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
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

		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public List<Course> getCourses(String tagName, CourseListSortValue sort, Boolean isAscending,
			Integer limit) {
		List<Course> result = new ArrayList<Course>();

		String defaultTemplate = "select * from Course c where c.collegeId=#bind($collegeId) and c.isWebVisible=true";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("collegeId", webSiteService.getCurrentCollege().getId());

		if (tagName != null) {
			List<Long> taggedIds = tagService.getEntityIdsByTagPath(tagName,
					Course.class.getSimpleName());
			if (taggedIds.isEmpty()) {
				return result;
			}
			defaultTemplate += " and c.id in (#bind($tagged))";
			parameters.put("tagged", taggedIds);

		}
		if (sort == null) {
			sort = CourseListSortValue.ALPHABETICAL;
		}

		// random list
		defaultTemplate += " order by rand()";

		SQLTemplate q = new SQLTemplate(Course.class, defaultTemplate);
		if (limit != null) {
			q.setFetchLimit(limit);
		}
		q.setParameters(parameters);

		result = cayenneService.sharedContext().performQuery(q);

		switch (sort) {
		case ALPHABETICAL:
			Ordering ordering = new Ordering(Course.NAME_PROPERTY,
					isAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING);
			ordering.orderList(result);
			break;
		case AVAILABILITY:
			sortByAvailability(isAscending, result);
			break;
		case DATE:
			sortByStartDate(isAscending, result);
			break;
		}
		return result;

	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()).andExp(getAvailabilityQualifier());
	}

	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true);
	}

	public List<Course> loadByIds(Object... ids) {

		if (ids == null || ids.length == 0) {
			return Collections.emptyList();
		}

		List<Course> result = new ArrayList<Course>(ids.length);
		for (Object id : ids) {
			result.add((Course) DataObjectUtils.objectForPK(cayenneService.sharedContext(),
					Course.class.getSimpleName(), id));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Course getCourse(String searchProperty, Object value) {
		College currentCollege = webSiteService.getCurrentCollege();
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY,
				currentCollege);
		if (searchProperty != null) {
			if (searchProperty.equals(Course.CODE_PROPERTY)) {
				qualifier = qualifier
						.andExp(getSearchStringPropertyQualifier(searchProperty, value));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(searchProperty, value));
			}
		}
		SelectQuery q = new SelectQuery(Course.class, qualifier);
		List<Course> result = sharedContext.performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	public Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public Course getCourse(String taggedWith) {
		List<Course> result = new ArrayList<Course>();

		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(getSiteQualifier());
		if (taggedWith != null) {
			q.andQualifier(getTaggedWithQualifier(taggedWith));
		}

		result = cayenneService.sharedContext().performQuery(q);

		return result.isEmpty() ? null : result.get(new Random().nextInt(result.size()));
	}

	private Expression getTaggedWithQualifier(String taggedWith) {
		return ExpressionFactory.inDbExp(Course.ID_PK_COLUMN,
				tagService.getEntityIdsByTagPath(taggedWith, Course.class.getSimpleName()));
	}

	public Integer getCoursesCount() {
		return ((Number) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery("select count(c) from Course c where "
								+ getSiteQualifier().toEJBQL("c"))).get(0)).intValue();
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery("select max(c.modified) from Course c where "
								+ getSiteQualifier().toEJBQL("c"))).get(0);
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
			browseTag = tagService.getSubTagByName(path.substring(path.lastIndexOf("/") + 1));
		} else {
			browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
			if (browseTag != null) {
				searchParams.put(SearchParam.subject, browseTag.getName());
			}
		}
		request.setAttribute("browseTag", browseTag);

		return searchParams;
	}

	private void sortByStartDate(final Boolean isAscending, List<Course> result) {
		Collections.sort(result, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.YEAR, 5);
				Date start1 = cal.getTime();
				for (CourseClass cc : o1.getCurrentClasses()) {
					Date startDate = cc.getStartDate();
					if (startDate != null && startDate.before(start1)) {
						start1 = startDate;
					}
				}

				Date start2 = cal.getTime();
				for (CourseClass cc : o2.getCurrentClasses()) {
					Date startDate = cc.getStartDate();
					if (startDate != null && startDate.before(start2)) {
						start2 = startDate;
					}
				}
				if (isAscending) {
					return start1.compareTo(start2);
				}
				return start2.compareTo(start1);
			}
		});
	}

	private void sortByAvailability(final Boolean isAscending, List<Course> result) {
		Collections.sort(result, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				Integer places1 = 0;
				for (CourseClass cc : o1.getCurrentClasses()) {
					places1 += cc.getAvailableEnrolmentPlaces();
				}

				Integer places2 = 0;
				for (CourseClass cc : o2.getCurrentClasses()) {
					places2 += cc.getAvailableEnrolmentPlaces();
				}
				if (isAscending) {
					return places1.compareTo(places2);
				}
				return places2.compareTo(places1);
			}
		});
	}

}
