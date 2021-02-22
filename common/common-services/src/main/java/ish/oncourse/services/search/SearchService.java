package ish.oncourse.services.search;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.solr.BuildSolrClient;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.query.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchService implements ISearchService {
	static final String TERMS_SEPARATOR_STRING = " || ";
	private static final String EVERY_DOCUMENT_MATCH_QUERY = "*:*";
	static final String SOLR_ANYTHING_AFTER_CHARACTER = "*";
	static final String SPACE_PATTERN = "[\\s]+";

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Maximum km distance for geo-searches, 19910 lead to Bad request for distance calculation.
	 */
	public static final double MAX_DISTANCE = 19900.0;


	/**
	 * The radius required for correct calculation of boost function for the cases when we want direct suburb matches filtering.
	 * When Website app generate the URL like websitehost/courses?near='some suburb name or postcode'&km=0
	 * this radius (10 centimeters) will be used to compensate the distance calculation error for boost function.
	 */
	public static final double MIN_DISTANCE = 0.0001;

	private IWebSiteService webSiteService;

	private ITagService tagService;

	private Map<SolrCollection, SolrClient> solrClients = new ConcurrentHashMap<>();


	private Properties appProperties;


	public SearchService(@Inject IWebSiteService webSiteService,
						 @Inject ITagService tagService) {

		this.webSiteService = webSiteService;
		this.tagService = tagService;
		this.appProperties = Configuration.loadProperties();
	}

	private SolrClient getSolrClient(SolrCollection core) {
		return solrClients.computeIfAbsent(core, k -> BuildSolrClient.instance(this.appProperties).build());
	}

	private QueryResponse query(SolrQuery q, SolrCollection core) throws Exception {
		return query(q, core, SolrRequest.METHOD.GET);
	}

	private QueryResponse query(SolrQuery q, SolrCollection core, SolrRequest.METHOD method) throws Exception {
		Exception exception;
		try {
			return getSolrClient(core).query(core.name(), q, method);
		} catch (Exception e) {
			exception = e;
			QueryResponse result = handleException(e, q);
			if (result != null)
				return result;
		}
		throw exception;
	}

	/**
	 * The method logs stacktraces every exception from hierarchy. I have added it to see full stack trace of a exception.
	 */
	private QueryResponse handleException(Throwable throwable, SolrQuery solrQuery) {
		logger.warn("Cannot execute query: {}", SearchResult.valueOf(solrQuery).getSolrQueryAsString(), throwable);

		if (throwable instanceof SolrServerException &&
				throwable.getCause() instanceof SolrException &&
				throwable.getCause().getMessage().contains("Bad Request")) {
			return new QueryResponse();
		} else {
			return null;
		}
	}

	@Override
	public Map<String, SolrDocumentList> searchClasses(SearchParams searchParams, Set<Long> coursesIds) {
		SolrQuery query = ClassesQueryBuilder.valueOf(searchParams, coursesIds).addFieldList("id score").enableGrouping().build();
		try {
			QueryResponse queryResponse = query(query, SolrCollection.classes, SolrRequest.METHOD.POST);
			ClassesGroupResponseParser responseParser = ClassesGroupResponseParser.valueOf(queryResponse.getGroupResponse(), "courseId");
			return responseParser.getCompactGroupsView();
		} catch (Exception e) {
			throw new SearchException("Error fetching classes from solr");
		}
	}
	
	public SearchResult searchCourses(SearchParams params, int start, Integer rows) {

		try {
			String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
			SolrQuery q = applyCourseRootTag(new SolrQueryBuilder().searchParams(params).collegeId(collegeId).start(start).rows(rows).build());
			SearchResult searchResult = SearchResult.valueOf(q, query(q, SolrCollection.courses));
			logger.debug("Solr query: {}", searchResult.getSolrQueryAsString());
			return searchResult;
		} catch (Exception e) {
			throw new SearchException("Unable to find courses.", e);
		}
	}

	public Map<Long, Long> getCountersForTags(SearchParams params, List<Tag> tags) {
		try {
			String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
			HashMap<Long, Long> result = new HashMap<>();

			for (Tag tag : tags) {
				TagGroups tagGroups = ish.oncourse.solr.query.TagGroups.valueOf(params, tag);
				SolrQuery q = applyCourseRootTag(new SolrQueryBuilder().searchParams(params).tagGroups(tagGroups).collegeId(collegeId).build());
				q.setFacet(true);
				String query = SolrQueryBuilder.getTagQuery(tag);
				q.addFacetQuery(query);
				QueryResponse response = query(q, SolrCollection.courses);
				Map<String, Integer> solrResult = response.getFacetQuery();
				result.put(tag.getId(), solrResult.get(query).longValue());
			}
			return result;
		} catch (Exception e) {
			throw new SearchException("Unable to get facet.", e);
		}
	}

	public Map<String, Count> getCountersForLocations(SearchParams params, List<Count> counts) {
		try {

			List<Suburb> suburbs = new ArrayList<>();
			for (Count count : counts) {
				Suburb suburb = SuburbParser.valueOf(count.getPath(), null, this).parse();
				suburbs.add(suburb);
			}

			LocationFacetQueryBuilder builder = LocationFacetQueryBuilder.valueOf(suburbs, counts, params, webSiteService.getCurrentCollege());
			builder.build();
			SolrQuery q = applyCourseRootTag(builder.getSolrQuery());
			Map<String, Count> queries = builder.getFacetQueries();

			QueryResponse response = query(q, SolrCollection.courses);

			HashMap<String, Count> result = new HashMap<>();
			Map<String, Integer> solrResult = response.getFacetQuery();
			for (Map.Entry<String, Integer> query : solrResult.entrySet()) {
				Count count = queries.get(query.getKey());
				result.put(count.getId(), Count.valueOf(count.getId(), count.getPath(), query.getValue()));
			}
			return result;
		} catch (Exception e) {
			throw new SearchException("Unable to get facet.", e);
		}
	}

	@Override
	public Integer getCounterForDuration(SearchParams params, Duration duration) {
		try {
	
			DurationFacetQueryBuilder builder = DurationFacetQueryBuilder.valueOf(duration, params, webSiteService.getCurrentCollege().getId());
			SolrQuery q = applyCourseRootTag(builder.build());
	
			QueryResponse response = query(q, SolrCollection.courses);
	
			return response.getFacetQuery().values().stream().findAny().orElse(0);			

		} catch (Exception e) {
			logger.error("Unable to get facet.");
			logger.catching(e);
			return 0;
		}
	}

	/**
	 * Apply course root tag for all the queries for Courses core if available.
	 *
	 * @param query - query to adjust
	 * @return query with added tag filter query.
	 */
	private SolrQuery applyCourseRootTag(SolrQuery query) {
		String coursesRootTagName = webSiteService.getCurrentWebSite().getCoursesRootTagName();
		if (StringUtils.trimToNull(coursesRootTagName) != null) {
			Tag tag = tagService.getTagByFullPath(coursesRootTagName);
			if (tag != null) {
				SolrQueryBuilder.appendFilterTag(query, tag);
			}
		}
		return query;
	}

	public SolrDocumentList autoSuggest(String term, String state) {
		try {
			College college = webSiteService.getCurrentCollege();
			AutoSuggestQueriesBuilder builder = AutoSuggestQueriesBuilder.valueOf(term, college, state).build();

			SolrDocumentList results = new SolrDocumentList();
			SolrQuery solrQuery = builder.getCoursesQuery();
			if (solrQuery != null) {
				QueryResponse coursesResults = query(applyCourseRootTag(solrQuery), SolrCollection.courses);
				if (coursesResults != null && coursesResults.getResults() != null && !coursesResults.getResults().isEmpty()) {
					results.addAll(coursesResults.getResults());
				}
			}
			solrQuery = builder.getSuburbsQuery();
			if (solrQuery != null) {
				QueryResponse suburbsResults = query(solrQuery, SolrCollection.suburbs);
				if (suburbsResults != null && suburbsResults.getResults() != null && !suburbsResults.getResults().isEmpty()) {
					results.addAll(suburbsResults.getResults());
				}
			}
			solrQuery = builder.getTagsQuery();
			if (solrQuery != null) {
				QueryResponse tagsResults = query(solrQuery, SolrCollection.tags);
				if (tagsResults != null && tagsResults.getResults() != null && !tagsResults.getResults().isEmpty()) {
					results.addAll(tagsResults.getResults());
				}
			}
			return results;
		} catch (Exception e) {
			logger.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	public SolrDocumentList searchSuburbs(String term) {
		return searchSuburbs(term, null);
	}


	public SolrDocumentList searchSuburbs(String term, String state) {
		try {
			SolrQuery q = new SolrQuery();

			StringBuilder query = new StringBuilder();

			String[] terms = term.split(SPACE_PATTERN);
			for (int i = 0; i < terms.length; i++) {
				if (StringUtils.trimToNull(terms[i]) != null) {
					String t = SolrQueryBuilder.replaceSOLRSyntaxisCharacters(terms[i].toLowerCase().trim()) + SOLR_ANYTHING_AFTER_CHARACTER;

					if (state != null) {
						query.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s) AND state:%s)", t, t, state));
					} else {
						query.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s)) ", t, t));
					}

					if (i + 1 != terms.length) {
						query.append(TERMS_SEPARATOR_STRING);
					}
				}
			}

			q.setQuery(query.toString());
			//in case of empty string search everything
			if (StringUtils.trimToNull(q.getQuery()) == null) {
				q.setQuery(EVERY_DOCUMENT_MATCH_QUERY);
			}
			SolrDocumentList results = new SolrDocumentList();
			QueryResponse suburbs = query(q, SolrCollection.suburbs);
			if (suburbs != null && suburbs.getResults() != null && !suburbs.getResults().isEmpty()) {
				results.addAll(suburbs.getResults());
			}
			return results;

		} catch (Exception e) {
			logger.error("Failed to search suburbs.", e);
			throw new SearchException("Unable to find suburbs.", e);
		}
	}

	public SolrDocumentList searchSuburb(String location) {
		try {
			SolrQuery solrQuery = SolrQueryBuilder.createSearchSuburbByLocationQuery(location);
			SolrDocumentList results = new SolrDocumentList();
			QueryResponse suburbs = query(solrQuery, SolrCollection.suburbs);
			SearchResult searchResult = SearchResult.valueOf(solrQuery, suburbs);
			logger.debug(searchResult.getSolrQueryAsString());
			if (suburbs != null && suburbs.getResults() != null && !suburbs.getResults().isEmpty()) {
				results.addAll(suburbs.getResults());
			}
			return results;
		} catch (Exception e) {
			logger.error("Failed to search suburb.", e);
			throw new SearchException("Unable to find suburb.", e);
		}
	}
}
