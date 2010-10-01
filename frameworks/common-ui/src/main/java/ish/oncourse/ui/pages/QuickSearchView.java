package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.PostcodeDb;
import ish.oncourse.model.Tag;
import ish.oncourse.services.search.ISearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.spatial.geohash.GeoHashUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class QuickSearchView {

	@Inject
	private Request request;
	@Inject
	private ISearchService searchService;
	@Property
	private String searchString;

	private String[] searchTerms;
	@Property
	private List<PostcodeDb> locationDetailList;
	@Property
	private PostcodeDb location;
	
	private List<Course> courses;
	
	@Property
	private List<Tag> tags;
	
	@Property
	private List<Course> matchingCourseList;
	
	@Property
	private List<Course> courseList;
	
	@Property
	private Course course;
	
	@Property
	private Tag tag;
	
	@Property
	private String searchingLocationsSearchString;

	@SetupRender
	void beforeRender() {
		searchString = request.getParameter("text");
		searchTerms = searchString.split("[\\s]+");
		QueryResponse suggestions = searchService.autoSuggest(searchString);
		setupLists(suggestions);

		setupSearchingLocationsSearchString();
		setupMatchingCourseList();
		setupCourseList();
	}

	/**
	 * @param suggestions
	 */
	private void setupLists(QueryResponse suggestions) {
		locationDetailList = new ArrayList<PostcodeDb>();
		courses = new ArrayList<Course>();
		tags = new ArrayList<Tag>();
		for (SolrDocument doc : suggestions.getResults()) {

			String doctype = (String) doc.get("doctype");

			if ("course".equalsIgnoreCase(doctype)) {
				Course course = new Course();
				course.setId(Long.valueOf((String) doc.get("id")));
				course.setName((String) doc.get("name"));
				course.setCode((String) doc.get("course_code"));
				courses.add(course);
			} else if ("place".equalsIgnoreCase(doctype)) {
				PostcodeDb postcodeDb = new PostcodeDb();
				postcodeDb.setSuburb((String) doc.get("suburb"));
				postcodeDb.setPostcode(Integer.valueOf((String) doc
						.get("postcode")));
				String[] points = ((String) doc.get("loc")).split(",");
				postcodeDb.setLat(Double.valueOf(points[0]));
				postcodeDb.setLon(Double.valueOf(points[1]));
				locationDetailList.add(postcodeDb);
			} else if ("tag".equals(doctype)) {
				Tag tag = new Tag();
				tag.setName((String) doc.get("name"));
				tags.add(tag);
			}
		}
	}

	public boolean isHasResults() {
		return isHasLocationDetailList() || isHasMatchingCourseList()
				|| isHasCourseList() || isHasTagGroupResultsList();
	}

	public boolean isHasLocationDetailList() {
		return !locationDetailList.isEmpty();
	}

	public boolean isHasMatchingCourseList() {
		return !matchingCourseList.isEmpty();
	}

	public boolean isHasCourseList() {
		return !courseList.isEmpty();
	}

	public boolean isHasTagGroupResultsList() {
		return !tags.isEmpty();
	}

	public boolean isHasSearchingLocationsSearchString() {
		return StringUtils.isNotBlank(searchingLocationsSearchString);
	}

	private void setupSearchingLocationsSearchString() {
		searchingLocationsSearchString = searchString;
		if (StringUtils.isNotBlank(searchingLocationsSearchString) && isHasLocationDetailList()) {
			searchingLocationsSearchString = getSearchStringWithoutLocation();
		}
		if (StringUtils.isBlank(searchingLocationsSearchString)) {
			searchingLocationsSearchString = "";
		}
	}

	public String getLocationLinkParameters() {
		return "near="
				+ GeoHashUtils.encode(location.getLat(), location.getLon())
				+ (!"".equals(searchingLocationsSearchString) ? "&s="
						+ searchingLocationsSearchString : "");
	}

	private String getSearchStringWithoutLocation() {
		List<String> locationTerms = new ArrayList<String>();
		for (PostcodeDb postcodeDb : locationDetailList) {
			locationTerms.add(postcodeDb.getSuburb());
			locationTerms.add(postcodeDb.getPostcode().toString());
		}
		StringBuilder buff = new StringBuilder();
		for (String term : searchTerms) {
			if (StringUtils.isNotBlank(term)) {
				String tempTerm = term.trim();
				boolean shouldContinue = false;
				for (String matchTerm : locationTerms) {
					if (tempTerm.equalsIgnoreCase(matchTerm)) {
						shouldContinue = true;
						break;
					}
				}
				if (shouldContinue) {
					continue;
				}
				if (buff.length() > 0) {
					buff.append(' ');
				}
				buff.append(term);
			}
		}
		return StringUtils.stripToNull(buff.toString());
	}

	private void setupMatchingCourseList() {
		matchingCourseList = new ArrayList<Course>();
		for (Course course : courses) {
			for (String term : searchTerms) {
				if (StringUtils.isNotBlank(term)) {
					String tempTerm = term.trim();
					tempTerm = term.indexOf("-") < 0 ? term : term.substring(0,
							term.indexOf("-"));
					if (course.getCode().equalsIgnoreCase(tempTerm)) {
						matchingCourseList.add(course);
						break;
					}

				}
			}
		}
	}

	private void setupCourseList() {
		List<Course> all = new ArrayList<Course>();
		all.addAll(courses);
		all.removeAll(matchingCourseList);
		courseList = new ArrayList<Course>();
		courseList.addAll(all);
	}

}
