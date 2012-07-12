package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchException;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.ui.components.CoursesList;
import ish.oncourse.ui.utils.SearchParamsParser;
import ish.oncourse.ui.utils.Suburb;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.CourseClassUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page component representing a Course list.
 * 
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


    public static final String ATTR_searchCoursesModel = "searchCoursesModel";


    @Inject
	private ICourseService courseService;
	@Inject
	private ISearchService searchService;
	@Inject
	private ITagService tagService;
	@Inject
	private ITextileConverter textileConverter;

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

	private Map<SearchParam, Object> searchParams;
	
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

	private List<Long> sitesIds;

    @Property
	private List<Suburb> suburbs;

    @InjectComponent
    private CoursesList coursesList;

	@SetupRender
	public void beforeRender() {
		int start = getIntParam(request.getParameter("start"), START_DEFAULT);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);
		sitesParameter = request.getParameter("sites");
		sitesIds = new ArrayList<Long>();
		if (sitesParameter == null) {
			sitesParameter = "";
		} else {
			String[] splittedSites = sitesParameter.split(",");
			for (String siteParam : splittedSites) {
				if (siteParam.indexOf("(") != -1) {
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
			this.courses = courseService.getCourses(start, rows);
			this.coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		}

		coursesIds = new ArrayList<Long>();
        initSuburbs();
		updateIdsAndIndexes();
	}

	private void updateIdsAndIndexes() {
		itemIndex = itemIndex + courses.size();
		for (Course course : courses) {
			if (!coursesIds.contains(course.getId()))
				coursesIds.add(course.getId());
		}
		setupMapSites();
	}
	
	public boolean isXHR() {
		return request.isXHR();
	}
	
	private void setupMapSites() {
		mapSites = new ArrayList<Site>();
		if (hasAnyFormValuesForFocus()) {
			focusesForMapSites = new HashMap<Long, Float>();
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
                                    suburbs != null && !suburbs.isEmpty() ? suburbs.get(0).getLatitude() : null,
                                    suburbs != null && !suburbs.isEmpty() ? suburbs.get(0).getLatitude() : null,
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

    private void initSuburbs() {
        if (searchParams != null && searchParams.containsKey(SearchParam.near)) {
            try {
                SolrDocumentList responseResults = (SolrDocumentList) searchParams.get(SearchParam.near);
                suburbs = new ArrayList<Suburb>();
                for (SolrDocument solrDocument: responseResults)
                {
                    suburbs.add(Suburb.valueOf(solrDocument, searchParams));
                }
            } catch (NumberFormatException e) {
                suburbs = null;
                LOGGER.warn(e);
            }
        }
    }

    private boolean hasAnyFormValuesForFocus() {
		if (searchParams == null) {
			return false;
		}
		return searchParams.containsKey(SearchParam.day) || searchParams.containsKey(SearchParam.near)
				|| searchParams.containsKey(SearchParam.price) || searchParams.containsKey(SearchParam.time);
	}

	public boolean isHasMapItemList() {
		return !mapSites.isEmpty();
	}

	private List<Course> searchCourses() {
		int start = getIntParam(request.getParameter("start"), itemIndex);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);

		searchParams = getCourseSearchParams();

        if (searchParams.containsKey(SearchParam.subject)) {
            request.setAttribute(Tag.BROWSE_TAG_PARAM, ((Tag)searchParams.get(SearchParam.subject)).getId());
        }
        return isHasInvalidSearchTerms() ? new ArrayList<Course>() : searchCourses(start, rows);
	}

	/**
	 * @param start
	 * @param rows
	 * @return
	 */
	private List<Course> searchCourses(int start, int rows) {

		QueryResponse resp = searchService.searchCourses(searchParams, start, rows);

		LOGGER.info(String.format("The number of courses found: %s", resp.getResults().size()));
		if (coursesCount == null) {
			coursesCount = ((Number) resp.getResults().getNumFound()).intValue();
		}

		List<String> ids = new ArrayList<String>(resp.getResults().size());

		for (SolrDocument doc : resp.getResults()) {
			ids.add((String) doc.getFieldValue("id"));
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

	public Map<SearchParam, Object> getCourseSearchParams() {
        SearchParamsParser searchParamsParser = new SearchParamsParser(request,searchService,tagService);
        searchParamsParser.parse();
        paramsInError = searchParamsParser.getParamsInError();
        return searchParamsParser.getSearchParams();
	}

	public Tag getBrowseTag() {
		return tagService.getBrowseTag();
	}

	public String getBrowseTagDetail() {
		return textileConverter.convertCustomTextile(getBrowseTag().getDetail(), new ValidationErrors());
	}
}
