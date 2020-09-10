package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoursesSkeleton extends ISHCommon {

	private static final Logger logger = LogManager.getLogger();

	private static final String SITES_PARAMETER_REGEX = "(\\d{1,})[,\\(]";

	private static final String SITES_PARAMETER_NAME = "sites";

	@Inject
	private ISearchService searchService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICourseService courseService;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private Boolean wasSearchException;

	@Property
	private List<Course> courses;

	@Property
	private Long coursesCount;

	@Property
	private SearchParams searchParams;

	@Property
	private Map<SearchParam, String> searchParamsErrors;

	@Inject
	private IRichtextConverter textileConverter;

	@Inject
	private IFacebookMetaProvider facebookMetaProvider;

	/**
	 * Needed for partial update of map, contains the sites which has been already loaded.
	 * sites=siteId1(focus1),siteId2,siteId3(focus3),...
	 */
	@Property
	private String sitesParameter;

	private List<Long> sitesIds;

	@Property
	private Map<Long, Float> focusesForMapSites;

	@Property
	private String debugInfo;

	@Property
	private Map debugInfoMap;

	@Property
	private List<Site> mapSites = new ArrayList<>();

	@SetupRender
	public void beforeRender() {

		parseSitesRequestParameter();
		parseSearchParams();

		try {
			courses = searchCourses();
		} catch (SearchException e) {
			logger.catching(e);
			wasSearchException = true;
			courses = new GetCourses(cayenneService.sharedContext(), webSiteService.getCurrentCollege()).get();
			coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		}
	}

	public boolean isWrongAnySearchParam() {
		return searchParamsErrors != null && !searchParamsErrors.isEmpty();
	}

	public boolean isMapSitesNotEmpty() {
		return !mapSites.isEmpty();
	}

	private List<Course> searchCourses() {

		if (searchParams.getSubject() != null) {
			request.setAttribute(Tag.BROWSE_TAG_PARAM, searchParams.getSubject().getId());
		}
		if (isWrongAnySearchParam()) {
			coursesCount = 0L;
			return new ArrayList<>();
		}

		SearchResult searchResult = searchService.searchCourses(searchParams, 0, Integer.MAX_VALUE);
		QueryResponse results = searchResult.getQueryResponse();
		SolrDocumentList list = results.getResults() != null ? results.getResults() : new SolrDocumentList();

		logger.info("The number of courses found: {}", list.size());

		if (coursesCount == null) {
			coursesCount = list.getNumFound();
		}

		List<String> ids = new ArrayList<>(list.size());

		for (SolrDocument doc : list) {
			ids.add((String) doc.getFieldValue("id"));
		}

		List<Course> courses = courseService.loadByIds(ids);
		addDebugInfo(searchResult, results, ids, courses);

		return courses;
	}

	private void addDebugInfo(SearchResult searchResult, QueryResponse results, List<String> ids, List<Course> courses) {
		if (results.getDebugMap() != null) {
			debugInfoMap = results.getExplainMap();

			//Move html to tpl file
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
	}

	//is this function used?
	private void setupMapSites() {
		boolean hasValuesForFocus = hasAnyFormValuesForFocus();
		if (hasValuesForFocus) {
			focusesForMapSites = new HashMap<>();
		}

		//This part will take time and memory and need to be refactored to single request to DB
		for (Course course : courses) {
			for (CourseClass courseClass : courseClassService.getEnrollableClasses(course)) {
				Room room = courseClass.getRoom();
				if (room != null) {
					Site site = room.getSite();
					if (site != null && site.getIsWebVisible() && site.getSuburb() != null && !"".equals(site.getSuburb()) && site.isHasCoordinates()) {

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

						if (hasValuesForFocus) {
							float focusMatchForClass = CourseClassUtils.focusMatchForClass(courseClass, searchParams);
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

	//Refactor to simple call to searchParams or SearchParamsParser
	private boolean hasAnyFormValuesForFocus() {
		return searchParams != null && (searchParams.getDay() != null ||
										searchParams.getSuburbs().size() > 0 ||
										searchParams.getPrice() != null ||
										searchParams.getTime() != null ||
										searchParams.getTutorId() != null);
	}

	private void parseSearchParams() {
		College currentCollege = webSiteService.getCurrentCollege();
		TimeZone timezone = webSiteService.getTimezone();
		SearchParamsParser searchParamsParser = SearchParamsParser.valueOf(request, currentCollege, searchService, tagService, timezone);
		searchParamsParser.parse();

		searchParamsErrors = searchParamsParser.getParamsInError();
		searchParams = searchParamsParser.getSearchParams();
	}

	private void parseSitesRequestParameter() {
		sitesIds = new ArrayList<>();

		sitesParameter = request.getParameter(SITES_PARAMETER_NAME);
		if (sitesParameter == null) {
			sitesParameter = StringUtils.EMPTY;
			return;
		}

		final Pattern pattern = Pattern.compile(SITES_PARAMETER_REGEX);
		final Matcher matcher = pattern.matcher(sitesParameter);

		while (matcher.find()) {
			sitesIds.add(Long.valueOf(matcher.group(1)));
		}
	}

	public String getBrowseTagDetail() {
		Tag browseTag = getBrowseTag();
		if(browseTag == null) {
			logger.error("Unexpected case: `browse tag` and `subject tag` both are null.");
			return "";
		}
		return textileConverter.convertCustomText(browseTag.getDetail(), new ValidationErrors());
	}

	public Tag getBrowseTag() {
		Tag tag = tagService.getBrowseTag();
		if (tag == null) {
			tag = tagService.getSubjectsTag();
		}
		return tag;
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

	public boolean isEnabledDebugInfo() {
		return debugInfo != null;
	}

	public boolean isCoursesEmpty() {
		return courses.isEmpty();
	}
}
