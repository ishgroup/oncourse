package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.auto._WebSite;
import ish.oncourse.services.courseclass.GetHideOnWebClassAge;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.courseclass.LoadByIds;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchResult;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.solr.query.SearchParams;
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

import static ish.oncourse.model.auto._Course.IS_WEB_VISIBLE;
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
	 * @deprecated use {@link GetCourses}
	 */
	@Deprecated
	public List<Course> getCourses(Integer startDefault, Integer rowsDefault) {
		if (startDefault == null)
			startDefault = START_DEFAULT;

		if (rowsDefault == null)
			rowsDefault = ROWS_DEFAULT;

		return new GetCourses(cayenneService.sharedContext(), webSiteService.getCurrentCollege()).offset(startDefault).limit(rowsDefault).get();
	}

	@Override
	public List<Course> getCourses(Tag tag, Sort sort, Boolean isAscending, Integer limit) {

		SearchParams searchParams = new SearchParams();
		searchParams.setClassAge(new GetHideOnWebClassAge().college(webSiteService.getCurrentCollege()).get());
		searchParams.setSubject(tag);
		SearchResult searchResult = searchService.searchCourses(searchParams, 0, limit);
		SolrDocumentList documents = searchResult.getQueryResponse().getResults();

		List<Long> list = new LinkedList<>();
		for (SolrDocument document : documents) {
			list.add(Long.valueOf(document.get(CourseClass.ID_PK_COLUMN).toString()));
		}

		Expression expr = ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, list);

		ObjectSelect<Course> q = ObjectSelect.query(Course.class)
				.where(expr)
				.and(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(Course.IS_WEB_VISIBLE.isTrue())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName());

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
	public String[] getAvailableSiteKeys(Course course) {
		return course.getCollege().getWebSites().stream().filter(s -> availableByRootTag(course, s)).map(WebSite::getSiteKey).toArray(String[]::new);
	}

	@Override
	public boolean availableByRootTag(Course course) {
		return availableByRootTag(course, webSiteService.getCurrentWebSite());
	}

	
	private boolean availableByRootTag(Course course, WebSite webSites) {
		Tag rootTag = tagService.getTagByFullPath(StringUtils.trimToNull(webSites.getCoursesRootTagName()));
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

	public List<Course> loadByIds(List<String> ids) {
		return LoadByIds.load(ids, cayenneService.sharedContext(), webSiteService.getCurrentCollege());
	}

	/**
	 * Get the first course which matches some search property. Use getCourseByCode() instead.
	 *
	 * @param searchProperty
	 * @param value
	 * @return
	 */
	@Deprecated
	public Course getCourse(String searchProperty, Object value) {

		Expression qualifier = Course.COLLEGE.eq(webSiteService.getCurrentCollege()).andExp(IS_WEB_VISIBLE.isTrue());

		if (searchProperty != null) {
			if (searchProperty.equals(Course.CODE_PROPERTY)) {
				qualifier = qualifier.andExp(getSearchStringPropertyQualifier(searchProperty, value));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(searchProperty, value));
			}
		}
		List<Course> courses = ObjectSelect.query(Course.class, qualifier)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.select(cayenneService.sharedContext());
		return courses.stream().findFirst().orElse(null);
	}

	public Course getCourseByCode(String code) {
		return ObjectSelect.query(Course.class)
				.where(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(Course.IS_WEB_VISIBLE.isTrue())
				.and(Course.CODE.eq(code))
				.cacheStrategy(LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext());
	}

	@Deprecated
	private Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	/**
	 * Get the first course with a particular tag. If there is more than one, then return a random course.
	 *
	 * @param taggedWith
	 * @return
	 */
	public Course getCourse(String taggedWith) {

		Expression qualifier = Course.COLLEGE.eq(webSiteService.getCurrentCollege()).andExp(IS_WEB_VISIBLE.isTrue());

		if (taggedWith != null) {
			qualifier = qualifier.andExp(getTaggedWithQualifier(taggedWith));
		}

		ObjectContext sharedContext = cayenneService.sharedContext();

		Long count = ObjectSelect.query(Course.class).count().where(qualifier)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(sharedContext);

		int random = count > 0 ? new Random().nextInt(count.intValue()) : -1;

		return random > -1 ?
				ObjectSelect.query(Course.class).where(qualifier)
						.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
						.limit(1).offset(random)
						.selectFirst(sharedContext) : null;
	}

	private Expression getTaggedWithQualifier(String taggedWith) {
		return ExpressionFactory.inDbExp(Course.ID_PK_COLUMN,
				tagService.getEntityIdsByTagPath(taggedWith, Course.class.getSimpleName()));
	}

	/**
	 * Count of all the courses for this site, without taking into account any site filter
	 *
	 * @return
	 */
	public Long getCoursesCount() {
		return ObjectSelect.query(Course.class).count()
				.where(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(IS_WEB_VISIBLE.isTrue())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext());
	}

	/**
	 * The most recently modified course date without taking into account any site filter
	 *
	 * @return
	 */
	public Date getLatestModifiedDate() {
		return ObjectSelect.query(Course.class).max(Course.MODIFIED)
				.where(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(IS_WEB_VISIBLE.isTrue())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.selectOne(cayenneService.sharedContext());
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


}
