package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchService implements ISearchService {
    static final String TERMS_SEPARATOR_STRING = " || ";
    static final String DIGIT_PATTERN = "(\\d)+";
    static final String EVERY_DOCUMENT_MATCH_QUERY = "*:*";
    static final String SOLR_ANYTHING_AFTER_CHARACTER = "*";
    static final String SOLR_OR_STRING = "||";
    static final String SPACE_PATTERN = "[\\s]+";
    static final String LIKE_CHARACTER = "%";

    private static final Logger logger = LogManager.getLogger();

    /**
     * Maximum km distance for geo-searches, 19910 lead to Bad request for distance calculation.
     */
    public static final double MAX_DISTANCE = 19900.0;

    /**
     * Default value which should be used if no distance specified.
     */
    public static final double DEFAULT_DISTANCE = 100.0;

    /**
     * The radius required for correct calculation of boost function for the cases when we want direct suburb matches filtering.
     * When Website app generate the URL like websitehost/courses?near='some suburb name or postcode'&km=0
     * this radius (10 centimeters) will be used to compensate the distance calculation error for boost function.
     */
    public static final double MIN_DISTANCE = 0.0001;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IPropertyService propertyService;

    @Inject
    private ILookupService lookupService;

    @SuppressWarnings("all")
    @Inject
    private ITagService tagService;

    private Map<SolrCore, SolrClient> solrClients = new HashMap<>();

    private SolrClient getSolrClient(SolrCore core) {
        SolrClient solrClient = null;
        if (solrClients.keySet().contains(core)) {
            solrClient = solrClients.get(core);
        }

        if (solrClient == null) {

            try {
                String solrURL = (String) lookupService.lookup(Property.SolrServer.value());

                solrURL = (solrURL == null) ? propertyService.string(Property.SolrServer) : solrURL;

                solrURL = (solrURL == null) ? System.getProperty(Property.SolrServer.value()) : solrURL;

                if (solrURL == null) {
                    throw new IllegalStateException("Undefined property: " + Property.SolrServer);
                }
                HttpSolrClient httpSolrClient = new HttpSolrClient(solrURL + "/" + core.toString());
                solrClient = httpSolrClient;
                solrClients.put(core, solrClient);

            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to solr server.", e);
            }

        }
        return solrClient;
    }

    /**
     * TODO the workaround for problem #14293. we should delete it this after the problem will be resolved
     *
     * @param q
     * @return
     * @throws SolrServerException
     */
    private QueryResponse query(SolrQuery q, SolrCore core) throws Exception {
        int count = 0;
        Exception exception = null;
        while (count < 3) {
            try {
                return getSolrClient(core).query(q);
            } catch (Exception e) {
                exception = e;
                count++;
                QueryResponse result = handleException(e, q, count);
                if (result != null)
                    return result;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
            }
        }
        if (exception != null)
            throw exception;
        else
            throw new IllegalArgumentException();
    }

    /**
     * The method logs stacktraces every exception from hierarchy. I have added it to see full stack trace of a exception.
     */
    private QueryResponse handleException(Throwable throwable, SolrQuery solrQuery, int count) {
        logger.warn("Cannot execute query: {} with attempt {}", SearchResult.valueOf(solrQuery).getSolrQueryAsString(), count, throwable);

        if (throwable instanceof SolrServerException &&
                throwable.getCause() instanceof SolrException &&
                throwable.getCause().getMessage().contains("Bad Request")) {
            return new QueryResponse();
        } else {
            return null;
        }
    }

    public SearchResult searchCourses(SearchParams params, int start, Integer rows) {

        try {
            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
            SolrQuery q = applyCourseRootTag(SolrQueryBuilder.valueOf(params, collegeId, start, rows).build());
            SearchResult searchResult = SearchResult.valueOf(q, query(q, SolrCore.courses));
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
                TagGroups tagGroups = TagGroups.valueOf(params, tag);
                SolrQuery q = applyCourseRootTag(SolrQueryBuilder.valueOf(params, tagGroups, collegeId).build());
                q.setFacet(true);
                String query = SolrQueryBuilder.getTagQuery(tag);
                q.addFacetQuery(query);
                QueryResponse response = query(q, SolrCore.courses);
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
            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
            SolrQuery q = applyCourseRootTag(SolrQueryBuilder.valueOf(params, collegeId).build());
            q.setFacet(true);

            Map<String, Count> queries = new HashMap<>();
            for (Count count : counts) {
                Suburb suburb = SuburbParser.valueOf(count.getPath(), null, this).parse();
                if (suburb != null) {
                    String query = SolrQueryBuilder.getSuburbQuery(suburb);
                    queries.put(query, count);
                    q.addFacetQuery(query);
                } else {
                    logger.debug(String.format("Cannot find suburb %s", count.getPath()));
                }
            }
            QueryResponse response = query(q, SolrCore.courses);

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

    public SolrDocumentList autoSuggest(String term) {
        try {
            College college = webSiteService.getCurrentCollege();
            AutoSuggestQueriesBuilder builder = AutoSuggestQueriesBuilder.valueOf(term, college).build();

            SolrDocumentList results = new SolrDocumentList();
            SolrQuery solrQuery = builder.getCoursesQuery();
            if (solrQuery != null) {
                QueryResponse coursesResults = query(applyCourseRootTag(solrQuery), SolrCore.courses);
                if (coursesResults != null && coursesResults.getResults() != null && !coursesResults.getResults().isEmpty()) {
                    results.addAll(coursesResults.getResults());
                }
            }
            solrQuery = builder.getSuburbsQuery();
            if (solrQuery != null) {
                QueryResponse suburbsResults = query(solrQuery, SolrCore.suburbs);
                if (suburbsResults != null && suburbsResults.getResults() != null && !suburbsResults.getResults().isEmpty()) {
                    results.addAll(suburbsResults.getResults());
                }
            }
            solrQuery = builder.getTagsQuery();
            if (solrQuery != null) {
                QueryResponse tagsResults = query(solrQuery, SolrCore.tags);
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
        try {
            SolrQuery q = new SolrQuery();

            StringBuilder query = new StringBuilder();

            String[] terms = term.split(SPACE_PATTERN);
            for (int i = 0; i < terms.length; i++) {
                if (StringUtils.trimToNull(terms[i]) != null) {
                    String t = SolrQueryBuilder.replaceSOLRSyntaxisCharacters(terms[i].toLowerCase().trim()) + SOLR_ANYTHING_AFTER_CHARACTER;

                    query.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s)) ", t, t));

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
            QueryResponse suburbs = query(q, SolrCore.suburbs);
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
            QueryResponse suburbs = query(solrQuery, SolrCore.suburbs);
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

    enum SolrCore {
        courses, suburbs, tags
    }
}
