package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.search.*;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.*;

/**
 * Page component representing a Course list.
 * <p/>
 * <p>
 * Corresponds to the page template at
 * <i>/common-ui/src/main/resources/ish.oncourse.ui.pages/Courses.tml</i>.
 * </p>
 *
 * @author ksenia
 */

public class Courses {

	@Inject
	@Property
	private Request request;

	private static final Logger LOGGER = Logger.getLogger(Courses.class);
	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 10;
	private static final String SOLR_DOCUMENT_ID_FIELD = "id";

	@Inject
	private ICourseService courseService;
	@Inject
	private ISearchService searchService;
	@Inject
	private ITagService tagService;
	@Inject
	private ITextileConverter textileConverter;
	@Inject
	private ICookiesService cookiesService;
	@Inject
	private IWebSiteService webSiteService;
	@Inject
	private IFacebookMetaProvider facebookMetaProvider;

	@Property
	private List<Course> courses;

	@Persist("client")
	private List<Long> coursesIds;

	@SuppressWarnings("all")
	@Property
	private Boolean isException;

	@Property
	private Integer coursesCount;

	@Property
	private Integer itemIndex;

	@Property
	private SearchParams searchParams;

	@Property
	private Map<SearchParam, String> paramsInError;

	@Property
	private List<Site> mapSites;

	@Property
	private Map<Long, Float> focusesForMapSites;
	/**
	 * needed for partial update of map, contains the sites which has been
	 * already loaded.has the structure:
	 * sites=siteId1(focus1),siteId2,siteId3(focus3),...
	 */
	@Property
	private String sitesParameter;

	@Property
	private String debugInfo;

	private List<Long> sitesIds;

	private List<Long> loadedCoursesIds;

	public boolean isDebugRequest() {
		return StringUtils.trimToNull(debugInfo) != null;
	}

	public List<Long> getPreviouslyLoadedCourseIds() {
		List<Long> loadedCourses = new ArrayList<>(coursesIds.size() + loadedCoursesIds.size());
		loadedCourses.addAll(loadedCoursesIds);
		loadedCourses.addAll(coursesIds);
		return loadedCourses;
	}

	@SetupRender
	public void beforeRender() {
		int start = getIntParam(request.getParameter("start"), START_DEFAULT);
		sitesParameter = request.getParameter("sites");
		sitesIds = new ArrayList<>();
		loadedCoursesIds = new ArrayList<>();
		if (isXHR() && start != 0) {
			String loadedCoursesIdsString = request.getParameter("loadedCoursesIds");
			if (StringUtils.trimToNull(loadedCoursesIdsString) != null) {
				String[] ids = loadedCoursesIdsString.split(",");
				for (String id : ids) {
					if (id.matches("\\d+")) {
						loadedCoursesIds.add(Long.valueOf(id));
					} else {
						LOGGER.warn(String.format("Incorrect loadedCoursesIds parameter passed. Unable to convert %s to long", id));
					}
				}
			}
		}
		if (sitesParameter == null) {
			sitesParameter = StringUtils.EMPTY;
		} else {
			String[] splittedSites = sitesParameter.split(",");
			for (String siteParam : splittedSites) {
				if (siteParam.contains("(")) {
					siteParam = siteParam.substring(0, siteParam.indexOf("("));
				}
				if (siteParam.matches("\\d+")) {
					sitesIds.add(Long.valueOf(siteParam));
				}
			}
		}

		this.itemIndex = start;
		this.isException = false;

		try {
			this.courses = searchCourses();
		} catch (SearchException e) {
			LOGGER.error("Unexpected search exception: " + e.getMessage(), e);
			this.isException = true;
			this.courses = courseService.getCourses(start, ROWS_DEFAULT);
			this.coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		}
		coursesIds = new ArrayList<>();
		updateIdsAndIndexes();
	}

	private void updateIdsAndIndexes() {
		itemIndex = itemIndex + courses.size();
		List<Course> duplicatedCourses = new ArrayList<>();
		for (Course course : courses) {
			Long courseId = course.getId();
			if (loadedCoursesIds.contains(courseId)) {
				duplicatedCourses.add(course);
			}
			if (!coursesIds.contains(courseId) && !loadedCoursesIds.contains(courseId)) {
				coursesIds.add(courseId);
			}
		}
		//remove duplicates from courses list
		while (!duplicatedCourses.isEmpty()) {
			Course course = duplicatedCourses.get(0);
			courses.remove(course);
			duplicatedCourses.remove(0);
			itemIndex--;
		}
		setupMapSites();
	}

	public boolean isXHR() {
		return request.isXHR();
	}

	private void setupMapSites() {
		mapSites = new ArrayList<>();
		if (hasAnyFormValuesForFocus()) {
			focusesForMapSites = new HashMap<>();
		}


		for (Course course : courses) {
			for (CourseClass courseClass : course.getEnrollableClasses()) {
				Room room = courseClass.getRoom();
				if (room != null) {
					Site site = room.getSite();
					if (site != null && site.getIsWebVisible() && site.getSuburb() != null && !"".equals(site.getSuburb())
							&& site.isHasCoordinates()) {
						boolean wasSitePreviouslyAdded = false;
						if (!mapSites.contains(site)) {
							mapSites.add(site);
							if (!sitesIds.contains(site.getId())) {
								if (!sitesParameter.equals("")) {
									sitesParameter += ",";
								}
								sitesParameter += site.getId();
							} else {
								wasSitePreviouslyAdded = true;
							}
						}
						if (hasAnyFormValuesForFocus()) {
							float focusMatchForClass = CourseClassUtils.focusMatchForClass(courseClass,
									searchParams);
							Float focusMatchForSite = focusesForMapSites.get(site.getId());
							if (focusMatchForSite == null || focusMatchForClass > focusMatchForSite) {
								focusesForMapSites.put(site.getId(), focusMatchForClass);
								if (!wasSitePreviouslyAdded) {
									sitesParameter += "(" + focusMatchForClass + ")";
								}
							}
						}
					}
				}
			}
		}

	}

	private boolean hasAnyFormValuesForFocus() {
		return searchParams != null &&
				(searchParams.getDay() != null ||
						searchParams.getNear() != null ||
						searchParams.getPrice() != null ||
						searchParams.getTime() != null);
	}

	public boolean isHasMapItemList() {
		return !mapSites.isEmpty();
	}

	private List<Course> searchCourses() {
		int start = getIntParam(request.getParameter("start"), itemIndex);

		searchParams = getCourseSearchParams();

		if (searchParams.getSubject() != null) {
			request.setAttribute(Tag.BROWSE_TAG_PARAM, searchParams.getSubject().getId());
		}

		if (isHasInvalidSearchTerms()) {
			coursesCount = 0;
			return new ArrayList<>();
		}
		return searchCourses(start, ROWS_DEFAULT);
	}

	private List<Course> searchCourses(int start, int rows) {
		QueryResponse results = searchService.searchCourses(searchParams, start, rows);
		SolrDocumentList list = results.getResults() != null ? results.getResults() : new SolrDocumentList();
		LOGGER.info(String.format("The number of courses found: %s", list.size()));
		if (coursesCount == null) {
			coursesCount = ((Number) list.getNumFound()).intValue();
		}
		List<String> ids = new ArrayList<>(list.size());
		for (SolrDocument doc : list) {
			ids.add((String) doc.getFieldValue(SOLR_DOCUMENT_ID_FIELD));
		}
		if (results.getDebugMap() != null) {
			debugInfo = results.getDebugMap().get("explain").toString();
		}
		return courseService.loadByIds(ids.toArray());
	}

	public boolean isHasAnyItems() {
		return !courses.isEmpty();
	}

	private static int getIntParam(String s, int def) {
		return (s != null && s.matches("\\d+")) ? Integer.parseInt(s) : def;
	}

	public boolean isHasInvalidSearchTerms() {
		return paramsInError != null && !paramsInError.isEmpty();
	}

	public SearchParams getCourseSearchParams() {
		SearchParamsParser searchParamsParser = new SearchParamsParser(request, searchService, tagService, getClientTimezone());
		searchParamsParser.parse();
		paramsInError = searchParamsParser.getParamsInError();
		return searchParamsParser.getSearchParams();
	}
	
	private TimeZone getClientTimezone() {
		TimeZone timezone = cookiesService.getClientTimezone();
		if (timezone == null) {
			timezone = cookiesService.getSimpleClientTimezone();
			if (timezone == null) {
				timezone = TimeZone.getTimeZone(webSiteService.getCurrentCollege().getTimeZone());
			}
		}
		return timezone;
	}

	public Tag getBrowseTag() {
		return tagService.getBrowseTag();
	}

	public String getBrowseTagDetail() {
		return textileConverter.convertCustomTextile(getBrowseTag().getDetail(), new ValidationErrors());
	}

	public String getMetaDescription() {
		
		if (getBrowseTag() !=null && getBrowseTag().getDetail() != null) {
			return facebookMetaProvider.getDescriptionContent(getBrowseTag());
		} else {
			return null;
		}
	}

	public String getCanonicalLinkPath() {
		return HTMLUtils.getCanonicalLinkPathForCourses(request, tagService.getBrowseTag());
	}
}
