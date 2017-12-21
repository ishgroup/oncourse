package ish.oncourse.services.course;

import ish.common.types.EntityRelationType;
import ish.oncourse.model.*;
import ish.oncourse.model.auto._CourseProductRelation;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.courseclass.LoadByIds;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SearchResult;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static ish.oncourse.model.auto._Course.*;
import static ish.oncourse.model.auto._CourseCourseRelation.FROM_COURSE;
import static ish.oncourse.model.auto._CourseCourseRelation.TO_COURSE;
import static ish.oncourse.model.auto._EntityRelation.FROM_ENTITY_IDENTIFIER;
import static ish.oncourse.model.auto._EntityRelation.TO_ENTITY_IDENTIFIER;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

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

		ObjectSelect<Course> q = ObjectSelect.query(Course.class)
				.where(getSiteQualifier());

		if (startDefault == null) {
			startDefault = START_DEFAULT;
		}

		if (rowsDefault == null) {
			rowsDefault = ROWS_DEFAULT;
		}

		q.offset(startDefault);
		q.limit(rowsDefault);
		return applyCourseCacheSettings(q).select(cayenneService.sharedContext());
	}

	@Override
	public List<Course> getCourses(Tag tag, Sort sort, Boolean isAscending, Integer limit) {

		SearchParams searchParams = new SearchParams();
		searchParams.setSubject(tag);
		SearchResult searchResult = searchService.searchCourses(searchParams, 0, limit);
		SolrDocumentList documents = searchResult.getQueryResponse().getResults();

		List<Long> list = new LinkedList<>();
		for (SolrDocument document : documents) {
			list.add(Long.valueOf(document.get(CourseClass.ID_PK_COLUMN).toString()));
		}

		Expression expr = ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, list).andExp(getSiteQualifier());

		ObjectSelect<Course> q = ObjectSelect.query(Course.class)
				.where(expr).cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName());

		List<Course> result = new ArrayList<>(q.select(cayenneService.sharedContext()));

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

		for (Tag tag : tagService.getTagsForEntity(Course.class.getSimpleName(), course.getId())) {
			if (rootTag.getId().equals(tag.getId()) || rootTag.isParentOf(tag)) {
				return true;
			}
		}
		return false;
	}

	private Expression getSiteQualifier() {
		return Course.COLLEGE.eq(webSiteService.getCurrentCollege()).andExp(getAvailabilityQualifier());
	}

	private Expression getAvailabilityQualifier() {
		return IS_WEB_VISIBLE.eq(true);
	}

	public List<Course> loadByIds(List<String> ids) {
		return LoadByIds.load(ids, cayenneService.sharedContext(), webSiteService.getCurrentCollege());
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
		return applyCourseCacheSettings(ObjectSelect.query(Course.class, qualifier)).selectFirst(cayenneService.sharedContext());
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
				.cacheStrategy(LOCAL_CACHE)
				.cacheGroup(Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext());
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

		Long count = ObjectSelect.query(Course.class).count().where(qualifier).cacheStrategy(QueryCacheStrategy.SHARED_CACHE, Course.class.getSimpleName()).selectOne(sharedContext);

		Course randomResult = null;
		int attempt = 0;

		while (randomResult == null && attempt++ < 5) {
			int random = new Random().nextInt(count.intValue());
			randomResult = applyCourseCacheSettings(ObjectSelect.query(Course.class).where(qualifier).offset(random).limit(1)).selectFirst(sharedContext);
		}
		return randomResult;
	}

	private Expression getTaggedWithQualifier(String taggedWith) {
		return ExpressionFactory.inDbExp(Course.ID_PK_COLUMN,
				tagService.getEntityIdsByTagPath(taggedWith, Course.class.getSimpleName()));
	}

	public Integer getCoursesCount() {
		return ObjectSelect.query(Course.class).count()
				.where(getSiteQualifier())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext()).intValue();
	}

	public Date getLatestModifiedDate() {
		return ObjectSelect.query(Course.class).max(Course.MODIFIED)
				.where(getSiteQualifier())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext());
	}

	public List<Product> getRelatedProductsFor(Course course) {
		ObjectContext context = cayenneService.sharedContext();

		return ObjectSelect.query(CourseProductRelation.class)
				.where(CourseProductRelation.COURSE.eq(course))
				.and(CourseProductRelation.PRODUCT.dot(Product.IS_WEB_VISIBLE).eq(TRUE))
				.cacheStrategy(LOCAL_CACHE, CourseProductRelation.class.getSimpleName()).select(context)
				.stream()
				.map(_CourseProductRelation::getProduct).collect(Collectors.toList());
	}

	public List<Course> getRelatedCoursesFor(Course course) {
		ObjectContext context = cayenneService.sharedContext();
		List<Course> courses = ObjectSelect.query(Course.class).where(
				TO_COURSES.outer().dot(FROM_COURSE).eq(course)
						.andExp(TO_COURSES.outer().dot(FROM_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE))
						.andExp(TO_COURSES.outer().dot(TO_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE)))
				.or(FROM_COURSES.outer().dot(TO_COURSE).eq(course)
						.andExp(FROM_COURSES.outer().dot(FROM_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE))
						.andExp(FROM_COURSES.outer().dot(TO_ENTITY_IDENTIFIER).eq(EntityRelationType.COURSE)))
				.and(IS_WEB_VISIBLE.eq(TRUE))
				.cacheStrategy(LOCAL_CACHE, CourseCourseRelation.class.getSimpleName())
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

	private static ObjectSelect<Course> applyCourseCacheSettings(ObjectSelect<Course> q) {
		return q.cacheStrategy(LOCAL_CACHE, Course.class.getSimpleName())
				.prefetch(Course.COURSE_CLASSES.joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.ROOM).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).dot(Tutor.CONTACT).joint())
				.prefetch(Course.COURSE_CLASSES.dot(CourseClass.DISCOUNT_COURSE_CLASSES).joint());
	}

}
