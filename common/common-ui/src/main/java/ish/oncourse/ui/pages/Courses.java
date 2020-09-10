package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.GetCourses;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.*;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.BlockNotFoundException;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class Courses extends ISHCommon {

	@Inject
	private Response response;

	private static final Logger logger = LogManager.getLogger();
	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 10;
	private static final String SOLR_DOCUMENT_ID_FIELD = "id";


	private static final String PARAM_UPDATE_COURSES_BY_FILTER = "updateCoursesByFilter";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICourseService courseService;
	@Inject
	private ISearchService searchService;
	@Inject
	private ITagService tagService;
	@Inject
	private IRichtextConverter textileConverter;
	@Inject
	private ICookiesService cookiesService;
	@Inject
	private IWebSiteService webSiteService;
	@Inject
	private IFacebookMetaProvider facebookMetaProvider;
	@Inject
	private ICourseClassService courseClassService;

	@Property
	private List<Course> courses;

	@Persist("client")
	private List<Long> coursesIds;

	@SuppressWarnings("all")
	@Property
	private Boolean isException;

	@Property
	private Long coursesCount;

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

	@Property
	private Map debugInfoMap;

	@Property
	private Block currentBlock;

	@Inject
	@Id("filteredCourses")
	private Block filteredCoursesBlock;

	@Inject
	@Id("moreCourses")
	private Block moreCoursesBlock;

	@Inject
	@Id("htmlCourses")
	private Block htmlCoursesBlock;

	private boolean isFilterRequest;

	private List<Long> sitesIds;

	private List<Long> loadedCoursesIds;

	public boolean isDebugRequest() {
		return debugInfo != null;
	}

	public List<Long> getPreviouslyLoadedCourseIds() {
		List<Long> loadedCourses = new ArrayList<>(coursesIds.size() + loadedCoursesIds.size());
		loadedCourses.addAll(loadedCoursesIds);
		loadedCourses.addAll(coursesIds);
		return loadedCourses;
	}

	@SetupRender
	public void beforeRender() {
		this.itemIndex = getIntParam(request.getParameter("start"), START_DEFAULT);
		this.isFilterRequest = Boolean.valueOf(request.getHeader(PARAM_UPDATE_COURSES_BY_FILTER));

		initCurrentBlock();

		sitesParameter = request.getParameter("sites");
		sitesIds = new ArrayList<>();
		loadedCoursesIds = new ArrayList<>();
		if (isXHR() && this.itemIndex != 0) {
			String loadedCoursesIdsString = request.getParameter("loadedCoursesIds");
			if (StringUtils.trimToNull(loadedCoursesIdsString) != null) {
				String[] ids = loadedCoursesIdsString.split(",");
				for (String id : ids) {
					if (id.matches("\\d+")) {
						loadedCoursesIds.add(Long.valueOf(id));
					} else {
						logger.warn("Incorrect loadedCoursesIds parameter passed. Unable to convert {} to long", id);
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

		this.isException = false;

		try {
			this.courses = searchCourses();
		} catch (SearchException e) {
			logger.catching(e);
			this.isException = true;
			this.courses = new GetCourses(cayenneService.sharedContext(), webSiteService.getCurrentCollege())
					.offset(itemIndex != null ? itemIndex : 0).limit(ROWS_DEFAULT).get();
			this.coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		}
		coursesIds = new ArrayList<>();
		updateIdsAndIndexes();
	}

	private void initCurrentBlock() {
		try {
			if (isXHR()) {
				if (this.isFilterRequest) {
					this.currentBlock = filteredCoursesBlock;
				} else {
					this.currentBlock = moreCoursesBlock;
				}
			} else {
				this.currentBlock = htmlCoursesBlock;
			}
		} catch (BlockNotFoundException e) {
			logger.debug("Template {} should be adjusted for the new implementation of Courses.class. College: {}:{}", "Courses.tml", webSiteService.getCurrentCollege().getName(), webSiteService.getCurrentCollege().getId(), e);
		}
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
			for (CourseClass courseClass : courseClassService.getEnrollableClasses(course)) {
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
						searchParams.getSuburbs().size() > 0 ||
						searchParams.getPrice() != null ||
						searchParams.getTime() != null ||
						searchParams.getTutorId() != null
				);
	}

	public boolean isHasMapItemList() {
		return !mapSites.isEmpty();
	}

	private List<Course> searchCourses() {
		searchParams = getCourseSearchParams();

		if (searchParams.getSubject() != null) {
			request.setAttribute(Tag.BROWSE_TAG_PARAM, searchParams.getSubject().getId());
		}

		if (isHasInvalidSearchTerms()) {
			coursesCount = 0L;
			return new ArrayList<>();
		}

		if (request.getParameter(HTMLUtils.VIEW_ALL_PARAM) != null && Boolean.TRUE.toString().equals(request.getParameter(HTMLUtils.VIEW_ALL_PARAM))) {
			return searchCourses(itemIndex, Integer.MAX_VALUE);
		}
		return searchCourses(itemIndex, ROWS_DEFAULT);
	}

	private List<Course> searchCourses(int start, int rows) {
		SearchResult searchResult = searchService.searchCourses(searchParams, start, rows);
		QueryResponse results = searchResult.getQueryResponse();
		SolrDocumentList list = results.getResults() != null ? results.getResults() : new SolrDocumentList();
		logger.info("The number of courses found: {}", list.size());
		if (coursesCount == null) {
			coursesCount = list.getNumFound();
		}
		List<String> ids = new ArrayList<>(list.size());
		for (SolrDocument doc : list) {
			ids.add((String) doc.getFieldValue(SOLR_DOCUMENT_ID_FIELD));
		}
		List<Course> courses = courseService.loadByIds(ids);
		if (results.getDebugMap() != null) {
			debugInfoMap = results.getExplainMap();
			debugInfo = String.format("<div class='debug-info-title'>Sorl query:</div>\n" +
							"<div class='debug-info-content'>%s</div>\n" +
							"<div class='debug-info-title'>Solr debug info:</div>\n" +
							"<div class='debug-info-content'>%s</div>\n" +
							"<div class='debug-info-title'>Solr result size:</div>\n" +
							"<div class='debug-info-content'>%d</div>\n" +
							"<div class='debug-info-title'>Solr list courses ids:</div>\n" +
							"<div class='debug-info-content'>%s</div>\n" +
							"<div class='debug-info-title'>DB found courses:</div>\n" +
							"<div class='debug-info-content'>%d</div>\n",
					searchResult.getSolrQueryAsString(),
					results.getDebugMap().get("explain").toString(),
					coursesCount,
					ids,
					courses.size()
			);
		}
		return courses;
	}

	public boolean isHasAnyItems() {
		return !courses.isEmpty();
	}
	

	public boolean isHasInvalidSearchTerms() {
		return paramsInError != null && !paramsInError.isEmpty();
	}

	public SearchParams getCourseSearchParams() {
		SearchParamsParser searchParamsParser = SearchParamsParser.valueOf(request, webSiteService.getCurrentCollege(),
				searchService, tagService, webSiteService.getTimezone());
		searchParamsParser.parse();
		paramsInError = searchParamsParser.getParamsInError();
		return searchParamsParser.getSearchParams();
	}

	public Tag getBrowseTag() {
		Tag tag = tagService.getBrowseTag();
		if (tag == null) {
			tag = tagService.getSubjectsTag();
			if (tag != null && tag.getIsWebVisible()) {
				return tag;
			}
		}
		return tag;
	}

	public String getBrowseTagDetail() {
		return textileConverter.convertCustomText(getBrowseTag().getDetail(), new ValidationErrors());
	}

	public String getMetaDescription() {

		if (getBrowseTag() != null && getBrowseTag().getDetail() != null) {
			return facebookMetaProvider.getDescriptionContent(getBrowseTag());
		} else {
			return null;
		}
	}

	public String getCanonicalLinkPath() {
		return HTMLUtils.getCanonicalLinkPathForCourses(request, tagService.getBrowseTag());
	}

	public String getCanonicalRelativeLinkPath() {
		return HTMLUtils.getCanonicalRelativeLinkPathForCourses(request, tagService.getBrowseTag());
	}
}
