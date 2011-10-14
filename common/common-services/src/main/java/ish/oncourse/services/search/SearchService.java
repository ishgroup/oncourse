package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SearchService implements ISearchService {

	private static final Logger logger = Logger.getLogger(SearchService.class);

	private static final int MAX_DISTANCE = 100;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IPropertyService propertyService;

	@Inject
	private ILookupService lookupService;

	@Inject
	private ITagService tagService;

	@Inject
	private Request request;

	private Map<SolrCore, SolrServer> solrServers = new HashMap<SolrCore, SolrServer>();

	private SolrServer getSolrServer(SolrCore core) {
		SolrServer solrServer = null;
		if (solrServers.keySet().contains(core)) {
			solrServer = solrServers.get(core);
		}

		if (solrServer == null) {

			try {
				String solrURL = (String) lookupService.lookup(Property.SolrServer.value());

				solrURL = (solrURL == null) ? propertyService.string(Property.SolrServer) : solrURL;

				solrURL = (solrURL == null) ? System.getProperty(Property.SolrServer.value()) : solrURL;

				if (solrURL == null) {
					throw new IllegalStateException("Undefined property: " + Property.SolrServer);
				}

				CommonsHttpSolrServer httpSolrServer = new CommonsHttpSolrServer(solrURL + "/" + core.toString());

				solrServer = httpSolrServer;
				solrServers.put(core, solrServer);

			} catch (Exception e) {
				throw new RuntimeException("Unable to connect to solr server.", e);
			}

		}
		return solrServer;
	}

	public QueryResponse searchCourses(Map<SearchParam, String> params, int start, int rows) {
		try {

			String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());

			SolrQuery q = new SolrQuery();

			q.setParam("fl", "id, name, course_loc");
			q.setStart(start);
			q.setRows(rows);
			q.setIncludeScore(true);
			q.addFilterQuery(String.format("+collegeId:%s +doctype:course end:[NOW TO *]", collegeId));

			if (params.size() == 1 && params.get(SearchParam.s) != null
					&& request.getAttribute(Tag.BROWSE_TAG_PARAM) == null) {
				q.setQuery(params.get(SearchParam.s).toLowerCase());
				q.setQueryType("dismax");
			} else {
				StringBuilder qString = new StringBuilder();

				if (params.containsKey(SearchParam.s)) {
					String s = params.get(SearchParam.s);
					qString.append(s).append(" ");
					qString.append(String.format("(detail: %s || tutor:%s || course_code:%s)", s, s, s)).append(" ");
				}
				
				if (params.containsKey(SearchParam.price)) {
					String price = params.get(SearchParam.price);
					if (price != null && price.length() > 0 && !StringUtils.isNumeric(price)) {
						// remove the possible currency sign
						price = price.replaceAll("[$]", "");
					}
					if(qString.length()!=0){
						qString.append(" AND ");
					}
					qString.append(String.format("price:[* TO %s]", price)).append(" ");
				}

				if (params.containsKey(SearchParam.day)) {
					String day = params.get(SearchParam.day);
					if(qString.length()!=0){
						qString.append(" AND ");
					}
					qString.append("when:" + day).append(" ");
				}

				if (params.containsKey(SearchParam.time)) {
					String time = params.get(SearchParam.time);
					if(qString.length()!=0){
						qString.append(" AND ");
					}
					qString.append("when:" + time).append(" ");
				}

				Tag browseTag = null;
				if (params.containsKey(SearchParam.subject)) {
					String subject = params.get(SearchParam.subject);
					browseTag = tagService.getTagByFullPath(subject);
				}

				if (browseTag != null) {
					StringBuilder tagQuery = new StringBuilder();
					tagQuery.append("(tagId:").append(browseTag.getId());

					for (Tag t : browseTag.getAllWebVisibleChildren()) {
						tagQuery.append(" || tagId:").append(t.getId());
					}
					if(qString.length()!=0){
						qString.append(" AND ");
					}
					qString.append(tagQuery.toString()).append(") ");
				}

				if (params.containsKey(SearchParam.near)) {
					String near = params.get(SearchParam.near);
					SolrDocumentList responseResults = searchSuburb(near).getResults();

					String location = (String) responseResults.get(0).get("loc");

					q.addFilterQuery("{!geofilt}");
					q.add("sfield", "course_loc");
					q.add("pt", location);
					q.add("d", "" + MAX_DISTANCE);

					q.addSortField("geodist()", ORDER.asc);

				}

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Solr query:%s", qString.toString()));
				}

				q.setQuery(qString.toString());
			}
			if ("".equals(q.getQuery())) {
				q.setQuery("*:*");
				q.setQueryType("standard");
			}
			return getSolrServer(SolrCore.courses).query(q);
		} catch (Exception e) {
			logger.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	public SolrDocumentList autoSuggest(String term) {
		try {

			College college = webSiteService.getCurrentCollege();
			String collegeId = String.valueOf(college.getId());

			StringBuilder coursesQuery = new StringBuilder();
			StringBuilder suburbsQuery = new StringBuilder();
			StringBuilder tagsQuery = new StringBuilder();

			String[] terms = term.split("[\\s]+");
			for (int i = 0; i < terms.length; i++) {
				String t = terms[i].toLowerCase().trim() + "*";
				coursesQuery.append(String.format("(name:%s && collegeId:%s)", t, collegeId)).append("||");

				coursesQuery.append(String.format("(course_code:%s && collegeId:%s)",
						t.indexOf("-") < 0 ? t : t.substring(0, t.indexOf("-")), collegeId));

				suburbsQuery.append(String.format("(doctype:suburb && (suburb:%s || postcode:%s)) ", t, t));

				tagsQuery.append(String.format("(doctype:tag && collegeId:%s && name:%s)", collegeId, t));

				if (i + 1 != terms.length) {
					coursesQuery.append(" || ");
					suburbsQuery.append(" || ");
					tagsQuery.append(" || ");
				}
			}

			SolrDocumentList results = new SolrDocumentList();
			if (coursesQuery.length() != 0) {
				results.addAll(getSolrServer(SolrCore.courses).query(new SolrQuery(coursesQuery.toString()))
						.getResults());
			}
			if (suburbsQuery.length() != 0) {
				results.addAll(getSolrServer(SolrCore.suburbs).query(new SolrQuery(suburbsQuery.toString()))
						.getResults());
			}
			if (tagsQuery.length() != 0) {
				results.addAll(getSolrServer(SolrCore.tags).query(new SolrQuery(tagsQuery.toString())).getResults());
			}
			return results;
		} catch (Exception e) {
			logger.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	public QueryResponse searchSuburbs(String term) {
		try {

			SolrQuery q = new SolrQuery();

			StringBuilder query = new StringBuilder();

			String[] terms = term.split("[\\s]+");
			for (int i = 0; i < terms.length; i++) {
				String t = terms[i].toLowerCase().trim() + "*";

				query.append(String.format("(doctype:suburb && (suburb:%s || postcode:%s)) ", t, t));

				if (i + 1 != terms.length) {
					query.append(" || ");
				}
			}

			q.setQuery(query.toString());
			if ("".equals(q.getQuery())) {
				q.setQuery("*:*");
			}
			return getSolrServer(SolrCore.suburbs).query(q);
		} catch (Exception e) {
			logger.error("Failed to search suburbs.", e);
			throw new SearchException("Unable to find suburbs.", e);
		}
	}

	public QueryResponse searchSuburb(String location) {
		try {
			int separator = location.lastIndexOf(" ");

			String[] suburbParams = separator > 0 ? new String[] { location.substring(0, separator),
					location.substring(separator + 1) } : new String[] { location, null };
			if (suburbParams[1] != null && !suburbParams[1].matches("(\\d)+")) {
				suburbParams[0] = location;
				suburbParams[1] = null;
			}
			SolrQuery q = new SolrQuery();

			StringBuilder query = new StringBuilder();
			query.append("(doctype:suburb");
			if (suburbParams[0] != null) {
				query.append(" && suburb:").append(suburbParams[0].replaceAll("[\\s]+", "+"));
			}

			if (suburbParams[1] != null) {
				query.append(" && postcode:").append(suburbParams[1]);
			}

			query.append(") ");

			q.setQuery(query.toString());

			return getSolrServer(SolrCore.suburbs).query(q);
		} catch (Exception e) {
			logger.error("Failed to search suburb.", e);
			throw new SearchException("Unable to find suburb.", e);
		}
	}

	enum SolrCore {
		courses, suburbs, tags
	}
}
