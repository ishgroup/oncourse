package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Site;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchException;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

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

	@Property
	private Boolean isException;

	@Property
	private Integer coursesCount;

	@Property
	private Integer itemIndex;

	private Map<SearchParam, String> searchParams;

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
		if (getCourseSearchParams().isEmpty()) {
			this.courses = courseService.getCourses(start, rows);
			this.coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		} else {
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
		}
		coursesIds = new ArrayList<Long>();

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

	private void setupMapSites() {
		mapSites = new ArrayList<Site>();
		if (hasAnyFormValuesForFocus()) {
			focusesForMapSites = new HashMap<Long, Float>();
		}
		Double[] locationPoints = { null, null };
		if (searchParams != null && searchParams.containsKey(SearchParam.near)) {
			try {
				String place = searchParams.get(SearchParam.near);
				SolrDocumentList responseResults = searchService.searchSuburb(place).getResults();
				if (!responseResults.isEmpty()) {
					SolrDocument doc = responseResults.get(0);
					String[] points = ((String) doc.get("loc")).split(",");
					locationPoints[0] = Double.parseDouble(points[0]);
					locationPoints[1] = Double.parseDouble(points[1]);
				}
			} catch (NumberFormatException e) {
			}
		}
		for (Course course : courses) {
			for (CourseClass courseClass : course.getEnrollableClasses()) {
				Room room = courseClass.getRoom();
				if (room != null) {
					Site site = room.getSite();
					if (site != null && site.getIsWebVisible() && site.getSuburb() != null
							&& !"".equals(site.getSuburb()) && site.isHasCoordinates()) {
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
							float focusMatchForClass = courseClass.focusMatchForClass(locationPoints[0],
									locationPoints[1], searchParams);
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

	public Map<SearchParam, String> getCourseSearchParams() {
		Map<SearchParam, String> searchParams = new HashMap<SearchParam, String>();

		paramsInError = new HashMap<SearchParam, String>();

		Tag browseTag = null;

		for (SearchParam name : SearchParam.values()) {
			String parameter = request.getParameter(name.name());
			if (parameter != null && !"".equals(parameter)) {
				searchParams.put(name, parameter);
				switch (name) {
				case day:
					if (!parameter.equalsIgnoreCase("weekday") && !parameter.equalsIgnoreCase("weekend")) {
						paramsInError.put(name, parameter);
					}
					break;
				case near:
					if (searchService.searchSuburb(parameter).getResults().isEmpty()) {
						paramsInError.put(name, parameter);
					}
					break;
				case price:
					if (!parameter.matches("[$]?(\\d)+[$]?")) {
						paramsInError.put(name, parameter);
					}
					break;
				case subject:
					browseTag = tagService.getTagByFullPath(parameter);
					if (browseTag == null) {
						paramsInError.put(name, parameter);
					}
					break;
				case time:
					if (!parameter.equalsIgnoreCase("daytime") && !parameter.equalsIgnoreCase("evening")) {
						paramsInError.put(name, parameter);
					}
					break;
				}
			}
		}

		if (browseTag == null && !paramsInError.keySet().contains(SearchParam.subject)) {
			browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
			if (browseTag != null) {
				searchParams.put(SearchParam.subject, browseTag.getDefaultPath());
			}
		}

		if (browseTag != null) {
			request.setAttribute(Tag.BROWSE_TAG_PARAM, browseTag.getId());
		}

		return searchParams;
	}

	public Tag getBrowseTag() {
		return tagService.getBrowseTag();
	}

	public String getBrowseTagDetail() {
		return textileConverter.convertCustomTextile(getBrowseTag().getDetail(), new ValidationErrors());
	}
}
