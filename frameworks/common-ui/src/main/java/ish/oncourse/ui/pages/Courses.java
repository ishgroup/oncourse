package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParam;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Courses {

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
	private Request request;

	@Property
	@Persist
	private List<Course> courses;

	@Property
	private Course course;

	@Property
	@Persist
	private Integer coursesCount;

	@Property
	@Persist
	private Integer itemIndex;

	@Persist
	private Map<SearchParam, String> searchParams;

	@Property
	@Persist
	private List<Site> mapSites;

	@Property
	@Persist
	private Map<Integer, Float> focusesForMapSites;

	@SetupRender
	public void beforeRender() {
		this.itemIndex = 0;
		if (request.getParameterNames().size() == 0
				&& request.getAttribute(Course.COURSE_TAG) == null) {
			this.courses = courseService
					.getCourses(START_DEFAULT, ROWS_DEFAULT);
			this.coursesCount = courseService.getCoursesCount();
			searchParams = null;
			focusesForMapSites = null;
		} else {
			this.courses = searchCourses();

		}
		this.itemIndex = courses.size();
		setupMapSites();
	}

	@InjectComponent
	private Zone coursesZone;

	@InjectComponent
	private Zone sitesMap;

	@OnEvent(component = "showMoreCourses")
	Object onActionFromShowMoreCourses() {
		if (searchParams == null) {
			courses.addAll(courseService.getCourses(itemIndex, ROWS_DEFAULT));
		} else {
			courses.addAll(searchCourses(itemIndex, ROWS_DEFAULT));
		}
		itemIndex = courses.size();
		return new MultiZoneUpdate("coursesZone", coursesZone).add("sitesMap",
				sitesMap);
	}

	private void setupMapSites() {
		mapSites = new ArrayList<Site>();
		if (hasAnyFormValuesForFocus()) {
			focusesForMapSites = new HashMap<Integer, Float>();
		}
		for (Course course : courses) {
			for (CourseClass courseClass : course.getEnrollableClasses()) {
				Room room = courseClass.getRoom();
				if (room != null) {
					Site site = room.getSite();
					if (site != null && site.getSuburb() != null
							&& !"".equals(site.getSuburb())
							&& site.getLatitude() != null
							&& site.getLongitude() != null) {
						if (!mapSites.contains(site)) {
							mapSites.add(site);
						}
						if (hasAnyFormValuesForFocus()) {
							float focusMatchForClass = focusMatchForClass(courseClass);
							Float focusMatchForSite = focusesForMapSites
									.get(site.getId());
							if (focusMatchForSite == null
									|| focusMatchForClass > focusMatchForSite) {
								focusesForMapSites.put(site.getId(),
										focusMatchForClass);
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
		return searchParams.containsKey(SearchParam.day)
				|| searchParams.containsKey(SearchParam.near)
				|| searchParams.containsKey(SearchParam.price)
				|| searchParams.containsKey(SearchParam.time);
	}

	private float focusMatchForClass(CourseClass courseClass) {
		float bestFocusMatch = -1.0f;

		if (!searchParams.isEmpty()) {

			float daysMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.day)) {
				daysMatch = courseClass.focusMatchForDays(searchParams
						.get(SearchParam.day));
			}

			float timeMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.time)) {
				timeMatch = courseClass.focusMatchForTime(searchParams
						.get(SearchParam.time));
			}

			float priceMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.price)) {
				try {
					Float price = Float.parseFloat(searchParams
							.get(SearchParam.price));
					priceMatch = courseClass.focusMatchForPrice(price);
				} catch (NumberFormatException e) {

				}
			}

			float nearMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.near)) {
				try {
					String place = searchParams.get(SearchParam.near);
					int separator = place.lastIndexOf(" ");
					if (separator > 0) {
						String[] suburbParams = {
								place.substring(0, separator),
								place.substring(separator + 1) };

						SolrDocumentList responseResults = searchService
								.searchSuburb(suburbParams[0], suburbParams[1])
								.getResults();
						if (!responseResults.isEmpty()) {
							SolrDocument doc = responseResults.get(0);
							String[] points = ((String) doc.get("loc"))
									.split(",");
							nearMatch = courseClass.focusMatchForNear(Double
									.parseDouble(points[0]), Double
									.parseDouble(points[1]));
						}
					}
				} catch (NumberFormatException e) {

				}
			}

			float result = daysMatch * timeMatch * priceMatch * nearMatch;

			return result;
		}

		return bestFocusMatch;

	}

	public boolean isHasMapItemList() {
		return !mapSites.isEmpty();
	}

	private List<Course> searchCourses() {
		int start = getIntParam(request.getParameter("start"), itemIndex);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);

		searchParams = new HashMap<SearchParam, String>();

		for (SearchParam name : SearchParam.values()) {
			String parameter = request.getParameter(name.name());
			if (parameter != null && !"".equals(parameter)) {
				searchParams.put(name, parameter);
			}
		}
		Tag browseTag = null;
		if (searchParams.containsKey(SearchParam.subject)) {
			String path = searchParams.get(SearchParam.subject);
			browseTag = tagService.getSubTagByName(path.substring(path
					.lastIndexOf("/") + 1));
		} else {
			browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
			if (browseTag != null) {
				searchParams.put(SearchParam.subject, browseTag.getName());
			}
		}
		request.setAttribute("browseTag", browseTag);
		if (searchParams.isEmpty()) {
			searchParams.put(SearchParam.s, "");
		}
		return searchCourses(start, rows);
	}

	/**
	 * @param start
	 * @param rows
	 * @return
	 */
	private List<Course> searchCourses(int start, int rows) {
		QueryResponse resp = searchService.searchCourses(searchParams, start,
				rows);

		LOGGER.info(String.format("The number of courses found: %s", resp
				.getResults().size()));
		if (coursesCount == null) {
			coursesCount = ((Number) resp.getResults().getNumFound())
					.intValue();
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

	public boolean isHasMoreItems() {
		return itemIndex < coursesCount;
	}

	private static int getIntParam(String s, int def) {
		int start = def;
		try {
			start = (s != null) ? Integer.parseInt(s) : start;
		} catch (Exception e) {
		}

		return start;
	}

	public boolean isHasInvalidSearchTerms() {
		// TODO CourseClassInMemoryFilter.hasInvalidSearchTermsForContext(
		// context() );
		return false;
	}
}
