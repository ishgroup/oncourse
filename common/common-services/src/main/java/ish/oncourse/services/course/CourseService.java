package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.persistence.ICayenneService;
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

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

@SuppressWarnings("unchecked")
public class CourseService implements ICourseService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	/**
	 * @see ICourseService#getCourses(Integer, Integer)
	 */
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

		appyCourseCacheSettings(q);

		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public List<Course> getCourses(String tagName, CourseListSortValue sort, Boolean isAscending, Integer limit) {
		List<Course> result = new ArrayList<Course>();

		String defaultTemplate = "select * from Course c where c.collegeId=#bind($collegeId) and c.isWebVisible=true";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("collegeId", webSiteService.getCurrentCollege().getId());

		if (tagName != null) {
			List<Long> taggedIds = tagService.getEntityIdsByTagPath(tagName, Course.class.getSimpleName());
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

		// TODO: uncomment when after upgrading to newer cayenne where
		// https://issues.apache.org/jira/browse/CAY-1585 is fixed.

		// q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		// q.setCacheGroups(CacheGroup.COURSES.name());

		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.ROOM_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.SESSIONS_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.TUTOR_ROLES_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.DISCOUNT_COURSE_CLASSES_PROPERTY);

		result = cayenneService.sharedContext().performQuery(q);

		switch (sort) {
		case ALPHABETICAL:
			Ordering ordering = new Ordering(Course.NAME_PROPERTY, isAscending ? SortOrder.ASCENDING
					: SortOrder.DESCENDING);
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
		return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
				getAvailabilityQualifier());
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

		final Map<Long, Integer> orderingMap = new HashMap<Long, Integer>();
		for (Integer i = 0; i < ids.length; i++) {
			Long id = null;
			if (ids[i] instanceof Long) {
				id = (Long) ids[i];
			}
			if (ids[i] instanceof String) {
				id = Long.valueOf((String) ids[i]);
			}
			orderingMap.put(id, i);
		}
		Expression expr = ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, ids).andExp(getSiteQualifier())
				.andExp(ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true));

		SelectQuery q = new SelectQuery(Course.class, expr);

		appyCourseCacheSettings(q);
		List<Course> courses = cayenneService.sharedContext().performQuery(q);
		Collections.sort(courses, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				return orderingMap.get(o1.getId()).compareTo(orderingMap.get(o2.getId()));
			}
		});
		return courses;
	}

	@SuppressWarnings("unchecked")
	public Course getCourse(String searchProperty, Object value) {

		Expression qualifier = getSiteQualifier();

		if (searchProperty != null) {
			if (searchProperty.equals(Course.CODE_PROPERTY)) {
				qualifier = qualifier.andExp(getSearchStringPropertyQualifier(searchProperty, value));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(searchProperty, value));
			}
		}

		SelectQuery q = new SelectQuery(Course.class, qualifier);

		appyCourseCacheSettings(q);

		return (Course) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	public Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public Course getCourse(String taggedWith) {

		Expression qualifier = getSiteQualifier();

		if (taggedWith != null) {
			qualifier = qualifier.andExp(getTaggedWithQualifier(taggedWith));
		}

		ObjectContext sharedContext = cayenneService.sharedContext();
		EJBQLQuery q = new EJBQLQuery("select count(i) from Course i where " + qualifier.toEJBQL("i"));
		Long count = (Long) sharedContext.performQuery(q).get(0);

		Course randomResult = null;
		int attempt = 0;

		while (randomResult == null && attempt++ < 5) {
			int random = new Random().nextInt(count.intValue());

			SelectQuery query = new SelectQuery(Course.class, qualifier);

			query.setFetchOffset(random);
			query.setFetchLimit(1);

			appyCourseCacheSettings(query);

			randomResult = (Course) Cayenne.objectForQuery(sharedContext, query);
		}
		return randomResult;
	}

	private Expression getTaggedWithQualifier(String taggedWith) {
		return ExpressionFactory.inDbExp(Course.ID_PK_COLUMN,
				tagService.getEntityIdsByTagPath(taggedWith, Course.class.getSimpleName()));
	}

	public Integer getCoursesCount() {
		return ((Number) cayenneService.sharedContext()
				.performQuery(new EJBQLQuery("select count(c) from Course c where " + getSiteQualifier().toEJBQL("c")))
				.get(0)).intValue();
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery("select max(c.modified) from Course c where " + getSiteQualifier().toEJBQL("c")))
				.get(0);
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

	private static void sortByAvailability(final Boolean isAscending, List<Course> result) {
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

	/**
	 * Add necessary prefetches and assign cache group for course query;
	 * 
	 * @param q
	 *            course query
	 */
	private static void appyCourseCacheSettings(SelectQuery q) {

		// TODO: uncomment when after upgrading to newer cayenne where
		// https://issues.apache.org/jira/browse/CAY-1585 is fixed.

		/**
		 * q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		 * q.setCacheGroups(CacheGroup.COURSES.name());
		 **/

		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.ROOM_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.SESSIONS_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.TUTOR_ROLES_PROPERTY);
		q.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.DISCOUNT_COURSE_CLASSES_PROPERTY);

	}
}
