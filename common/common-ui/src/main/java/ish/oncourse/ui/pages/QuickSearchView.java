package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.PostcodeDb;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchException;
import ish.oncourse.services.tag.ITagService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class QuickSearchView {

	private static final String TAG_DOCTYPE = "tag";

	private static final String SUBURB_DOCTYPE = "suburb";

	private static final String COURSE_DOCTYPE = "course";

	private static final String SOLR_DOCUMENT_DOCTYPE_FIELD = "doctype";

	private static final Logger logger = LogManager.getLogger();
	
	private static final String SOLR_DOCUMENT_ID_FIELD = "id";

	@Inject
	private Request request;

	@Inject
	private ISearchService searchService;

	@Inject
	private ITagService tagService;

	@Inject
	private ICourseService courseService;

	@Inject
	private IPostCodeDbService postCodeDbService;
	
	@Property
	private String searchString;

	private String[] searchTerms;
	@Property
	private List<PostcodeDb> locationDetailList;
	@Property
	private PostcodeDb location;

	@Property
	private List<Tag> tags;

	private List<Course> courses;

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
		if (searchString != null) {
			searchTerms = searchString.split("[\\s]+");
			try {
				SolrDocumentList suggestions = searchService.autoSuggest(searchString);
				setupLists(suggestions);
				setupSearchingLocationsSearchString();
				setupMatchingCourseList();
				setupCourseList();
			} catch (SearchException e) {
				logger.error("search exception occurred at {}", request.getServerName(), e);
				nullifySearch();
			}
		} else {
			logger.error("quick search plugin invoked in application.js works incorrectly for {} (request brings empty text parameter)", request.getServerName());
			nullifySearch();
		}
	}

	private void nullifySearch() {
		searchString = "";
		searchTerms = null;
		setupLists(new SolrDocumentList());
		matchingCourseList = new ArrayList<>();
		setupCourseList();
	}

	/**
	 * @param suggestions
	 */
	private void setupLists(SolrDocumentList suggestions) {
		locationDetailList = new ArrayList<>();
		List<String> courseIds = new ArrayList<>();
		List<String> tagIds = new ArrayList<>();
		List<String> postCodes = new ArrayList<>();
		
		for (SolrDocument doc : suggestions) {
			String doctype = (String) doc.get(SOLR_DOCUMENT_DOCTYPE_FIELD);
			if (COURSE_DOCTYPE.equalsIgnoreCase(doctype)) {
				courseIds.add((String) doc.get(SOLR_DOCUMENT_ID_FIELD));
			} else if (SUBURB_DOCTYPE.equalsIgnoreCase(doctype)) {
				postCodes.add((String) doc.get(SUBURB_DOCTYPE));
			} else if (TAG_DOCTYPE.equals(doctype)) {
				tagIds.add((String) doc.get(SOLR_DOCUMENT_ID_FIELD));
			}
		}

		this.courses = courseService.loadByIds(courseIds.toArray());
		this.tags = tagService.loadByIds(tagIds.toArray());
		this.locationDetailList = postCodeDbService.findBySuburb(postCodes.toArray(new String[postCodes.size()]));
	}

	public boolean isHasResults() {
		return isHasLocationDetailList() || isHasMatchingCourseList() || isHasCourseList()
				|| isHasTagGroupResultsList();
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

	private String getSearchStringWithoutLocation() {
		List<String> locationTerms = new ArrayList<>();
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
		matchingCourseList = new ArrayList<>();
		for (Course course : courses) {
			for (String term : searchTerms) {
				if (StringUtils.isNotBlank(term)) {
					String tempTerm = term.trim();
					tempTerm = !term.contains("-") ? term : term.substring(0, term.indexOf("-"));
					if (course.getCode().equalsIgnoreCase(tempTerm)) {
						matchingCourseList.add(course);
						break;
					}

				}
			}
		}
	}

	private void setupCourseList() {
		List<Course> all = new ArrayList<>();
		all.addAll(courses);
		all.removeAll(matchingCourseList);
		courseList = new ArrayList<>();
		courseList.addAll(all);
	}

}
