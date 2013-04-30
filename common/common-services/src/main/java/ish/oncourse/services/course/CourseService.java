package ish.oncourse.services.course;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.attrs.CourseListSortValue;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

@SuppressWarnings("unchecked")
public class CourseService implements ICourseService {

    private static final Logger LOGGER = Logger.getLogger(CourseService.class);

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

		applyCourseCacheSettings(q);
		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public List<Course> getCourses(String tagName, CourseListSortValue sort, Boolean isAscending, Integer limit) {
		List<Course> result = new ArrayList<>();
		Long collegeId = webSiteService.getCurrentCollege().getId();
		Expression expression = ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true)
			.andExp(ExpressionFactory.matchDbExp(Course.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, collegeId));
		if (tagName != null) {
			List<Long> taggedIds = tagService.getEntityIdsByTagPath(tagName, Course.class.getSimpleName());
			if (taggedIds.isEmpty()) {
				return result;
			}
			expression = expression.andExp(ExpressionFactory.inDbExp(Course.ID_PK_COLUMN, taggedIds));
		}
		SelectQuery query = new SelectQuery(Course.class, expression);
		
		// TODO: uncomment when after upgrading to newer cayenne where
		// https://issues.apache.org/jira/browse/CAY-1585 is fixed.

		// query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		// query.setCacheGroups(CacheGroup.COURSES.name());

		query.addPrefetch(Course.COURSE_CLASSES_PROPERTY);
		query.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.ROOM_PROPERTY);
		query.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.SESSIONS_PROPERTY);
		query.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.TUTOR_ROLES_PROPERTY);
		query.addPrefetch(Course.COURSE_CLASSES_PROPERTY + "." + CourseClass.DISCOUNT_COURSE_CLASSES_PROPERTY);

		result = cayenneService.sharedContext().performQuery(query);
		if (sort == null) {
			//if nothing specified use default
			sort = CourseListSortValue.ALPHABETICAL;
		}
		switch (sort) {
		case AVAILABILITY:
			sortByAvailability(isAscending, result);
			break;
		case DATE:
			sortByStartDate(isAscending, result);
			break;
		case ALPHABETICAL:
			Ordering ordering = new Ordering(Course.NAME_PROPERTY, isAscending ? SortOrder.ASCENDING
					: SortOrder.DESCENDING);
			ordering.orderList(result);
			break;
		}
		if (limit != null && result.size() > limit) {
			return result.subList(0, limit);
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

		final Map<Long, Integer> orderingMap = new HashMap<>();
		for (Integer i = 0; i < ids.length; i++) {
			Long id = null;
			if (ids[i] instanceof Long) {
				id = (Long) ids[i];
			}
            //To exclude NumberFormatException  StringUtils.isNumeric has been added
			if (ids[i] instanceof String &&
                    StringUtils.trimToNull((String) ids[i]) != null &&
                    StringUtils.isNumeric((String) ids[i])) {
				id = Long.valueOf((String) ids[i]);
			}
            if (id != null)
            {
			    orderingMap.put(id, i);
            }
            else
            {
                /**
                 * The warn has been added to exclude error when some hacked request is going with not numeric ids
                 */
                LOGGER.warn(String.format("ids cannot contain not numeric element like this: %s",ids[i]));
                return Collections.EMPTY_LIST;
            }
		}
		Expression expr = ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, orderingMap.keySet()).andExp(getSiteQualifier())
				.andExp(ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true));

		SelectQuery q = new SelectQuery(Course.class, expr);

		applyCourseCacheSettings(q);
		List<Course> courses = cayenneService.sharedContext().performQuery(q);
		Collections.sort(courses, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				return orderingMap.get(o1.getId()).compareTo(orderingMap.get(o2.getId()));
			}
		});
		return courses;
	}

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

		applyCourseCacheSettings(q);

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

			applyCourseCacheSettings(query);

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
	private static void applyCourseCacheSettings(SelectQuery q) {

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
		//q.addPrefetch(Course.QUALIFICATION_PROPERTY);
		//q.addPrefetch(Course.COURSE_MODULES_PROPERTY);
		//q.addPrefetch(Course.COURSE_MODULES_PROPERTY + "." + CourseModule.MODULE_PROPERTY);
	}
}
