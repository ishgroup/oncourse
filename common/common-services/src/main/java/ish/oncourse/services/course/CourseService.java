package ish.oncourse.services.course;

import ish.common.types.EntityRelationType;
import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.courseclass.LoadByIds;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SearchResult;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

import static ish.oncourse.model.auto._Course.*;
import static ish.oncourse.model.auto._CourseCourseRelation.FROM_COURSE;
import static ish.oncourse.model.auto._CourseCourseRelation.TO_COURSE;
import static ish.oncourse.model.auto._EntityRelation.FROM_ENTITY_IDENTIFIER;
import static ish.oncourse.model.auto._EntityRelation.TO_ENTITY_IDENTIFIER;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE;

public class CourseService implements ICourseService {

    private static final Logger logger = LogManager.getLogger();

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	@Inject
	private ISearchService searchService;

	@Inject
	private ICourseClassService courseClassService;

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
		return cayenneService.newContext().performQuery(q);
	}

	@Override
	public List<Course> getCourses(Tag tag, Sort sort, Boolean isAscending, Integer limit) {

		SearchParams searchParams = new SearchParams();
		searchParams.setSubject(tag);
		SearchResult searchResult = searchService.searchCourses(searchParams,0, limit);
		SolrDocumentList documents = searchResult.getQueryResponse().getResults();

		List<Long> list = new LinkedList<>();
		for (SolrDocument document : documents) {
			list.add(Long.valueOf(document.get(CourseClass.ID_PK_COLUMN).toString()));
		}

		Expression expr = ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN,list).andExp(getSiteQualifier());

		SelectQuery q = new SelectQuery(Course.class, expr);
		q.setCacheStrategy(SHARED_CACHE);
		q.setCacheGroup(Course.class.getSimpleName());

		List<Course> result = cayenneService.newContext().performQuery(q);

		if (sort == null) {
			//if nothing specified use default
			sort = Sort.alphabetical;
		}
		switch (sort) {
		case availability:
			sortByAvailability(isAscending, result);
			break;
		case date:
			sortByStartDate(isAscending, result);
			break;
		case alphabetical:
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

	@Override
	public boolean availableByRootTag(Course course) {
		Tag rootTag = tagService.getTagByFullPath(StringUtils.trimToNull(webSiteService.getCurrentWebSite().getCoursesRootTagName()));
		if (rootTag == null) {
			return true;
		}
		
		for (Tag tag :  tagService.getTagsForEntity(Course.class.getSimpleName(), course.getId())) {
			if (rootTag.getId().equals(tag.getId()) || rootTag.isParentOf(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return Course.COLLEGE.eq(webSiteService.getCurrentCollege()).andExp(getAvailabilityQualifier());
	}

	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return IS_WEB_VISIBLE.eq(true);
	}

	public List<Course> loadByIds(List<String> ids) {
		return LoadByIds.load(ids, cayenneService.newContext(), webSiteService.getCurrentCollege());
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

		return (Course) Cayenne.objectForQuery(cayenneService.newContext(), q);
	}

	public Course getCourseByCode(String code) {
		return ObjectSelect.query(Course.class)
				.where(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(Course.IS_WEB_VISIBLE.isTrue())
				.and(Course.CODE.eq(code))
				.prefetch(Course.COURSE_CLASSES.joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.COLLEGE).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.ROOM).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.ROOM).dot(Room.SITE).joint())
				.cacheStrategy(SHARED_CACHE)
				.cacheGroup(Course.class.getSimpleName())
				.selectOne(cayenneService.newContext());
	}
	
	public Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public Course getCourse(String taggedWith) {

		Expression qualifier = getSiteQualifier();

		if (taggedWith != null) {
			qualifier = qualifier.andExp(getTaggedWithQualifier(taggedWith));
		}

		ObjectContext sharedContext = cayenneService.newContext();
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
		return ((Number) cayenneService.newContext()
				.performQuery(new EJBQLQuery("select count(c) from Course c where " + getSiteQualifier().toEJBQL("c")))
				.get(0)).intValue();
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.newContext()
				.performQuery(
						new EJBQLQuery("select max(c.modified) from Course c where " + getSiteQualifier().toEJBQL("c")))
				.get(0);
	}

    public List<Product> getRelatedProductsFor(Course course) {
        ObjectContext context = cayenneService.newContext();
        SelectQuery query = new SelectQuery(CourseProductRelation.class,
                ExpressionFactory.matchExp(CourseProductRelation.COURSE_PROPERTY, course)
                        .andExp(ExpressionFactory.matchExp(CourseProductRelation.PRODUCT_PROPERTY + "." + Product.IS_WEB_VISIBLE_PROPERTY, TRUE)));
		query.setCacheStrategy(SHARED_CACHE);
		query.setCacheGroup(CourseProductRelation.class.getSimpleName());
        List<CourseProductRelation> relations = context.performQuery(query);
        ArrayList<Product> result = new ArrayList<>();
        for (CourseProductRelation relation : relations) {
            result.add(relation.getProduct());
        }
        return result;
    }

	public List<Course> getRelatedCoursesFor(Course course) {
		ObjectContext context = cayenneService.newContext();
		List<Course> courses = ObjectSelect.query(Course.class).where(
				TO_COURSES.outer().dot(FROM_COURSE).eq(course)
						.andExp(TO_COURSES.outer().dot(FROM_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE))
						.andExp(TO_COURSES.outer().dot(TO_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE)))
				.or(FROM_COURSES.outer().dot(TO_COURSE).eq(course)
						.andExp(FROM_COURSES.outer().dot(FROM_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE))
						.andExp(FROM_COURSES.outer().dot(TO_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE)))
				.and(IS_WEB_VISIBLE.eq(TRUE))
				.cacheStrategy(SHARED_CACHE, CourseCourseRelation.class.getSimpleName())
				.orderBy(Course.NAME.asc())
				.select(context);
		return courses;
	}

	private void sortByStartDate(final Boolean isAscending, List<Course> result) {
		Collections.sort(result, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.YEAR, 5);
				Date start1 = cal.getTime();
				for (CourseClass cc : courseClassService.getCurrentClasses(o1)) {
					Date startDate = cc.getStartDate();
					if (startDate != null && startDate.before(start1)) {
						start1 = startDate;
					}
				}

				Date start2 = cal.getTime();
				for (CourseClass cc : courseClassService.getCurrentClasses(o2)) {
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
				for (CourseClass cc : courseClassService.getCurrentClasses(o1)) {
					places1 += cc.getAvailableEnrolmentPlaces();
				}

				Integer places2 = 0;
				for (CourseClass cc : courseClassService.getCurrentClasses(o2)) {
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
     * @param q course query
	 */
	private static void applyCourseCacheSettings(SelectQuery q) {
		q.setCacheStrategy(SHARED_CACHE);
		q.setCacheGroup(Course.class.getSimpleName());
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
