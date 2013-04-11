package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SearchService implements ISearchService {
	private static final String TERMS_SEPARATOR_STRING = " || ";
	static final String DIGIT_PATTERN = "(\\d)+";
	static final String EVERY_DOCUMENT_MATCH_QUERY = "*:*";
	static final String SOLR_ANYTHING_AFTER_CHARACTER = "*";
	static final String SOLR_OR_STRING = "||";
	static final String SPACE_PATTERN = "[\\s]+";
	static final String LIKE_CHARACTER = "%";

	private static final Logger logger = Logger.getLogger(SearchService.class);

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
                //TODO: after update solrj dependency to 4.0 we need to use HttpSolrServer class instead of CommonsHttpSolrServer
                //HttpSolrServer httpSolrServer = new HttpSolrServer(solrURL + "/" + core.toString());
                CommonsHttpSolrServer httpSolrServer = new CommonsHttpSolrServer(solrURL + "/" + core.toString());

                solrServer = httpSolrServer;
                solrServers.put(core, solrServer);

            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to solr server.", e);
            }

        }
        return solrServer;
    }

    /**
     * TODO the workaround for problem #14293. we should delete it this after the problem will be resolved
     * @param q
     * @return
     * @throws SolrServerException
     */
    private QueryResponse query(SolrQuery q, SolrCore core) throws SolrServerException {
        int count = 0;
        SolrServerException exception = null;
        while (count < 3) {
            try {
                return getSolrServer(core).query(q);
            } catch (SolrServerException e) {
                exception = e;
                count++;
                QueryResponse result = handleException(e,q,count);
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
    private QueryResponse handleException(Throwable throwable, SolrQuery solrQuery, int count)
    {
        logger.warn(String.format("Cannot execute query: %s with attempt %d",solrQueryToString(solrQuery),count), throwable);

        if (throwable instanceof SolrServerException &&
                throwable.getCause() instanceof SolrException &&
                throwable.getCause().getMessage().contains("Bad Request"))
        {
            return new QueryResponse();
        }
        else
            return null;
    }

    public QueryResponse searchCourses(SearchParams params, int start, int rows) {

        try {
            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
            SolrQuery q = new SolrQueryBuilder(params, collegeId, start, rows).create();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Solr query:%s", solrQueryToString(q)));
            }
            return query(q, SolrCore.courses);
        } catch (Exception e) {
            throw new SearchException("Unable to find courses.", e);
        }
    }
    
    private String solrQueryToString(SolrQuery q) {
        try {
            return URLDecoder.decode(q.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return q.toString();
        }
    }
    
    public SolrDocumentList autoSuggest(String term) {
    	try {
            College college = webSiteService.getCurrentCollege();
            String collegeId = String.valueOf(college.getId());

            StringBuilder coursesQuery = new StringBuilder();
            StringBuilder suburbsQuery = new StringBuilder();
            StringBuilder tagsQuery = new StringBuilder();

            String[] terms = term.split(SPACE_PATTERN);
            for (int i = 0; i < terms.length; i++) {
                if (StringUtils.trimToNull(SolrQueryBuilder.replaceSOLRSyntaxisCharacters(terms[i])) != null) {
                    String t = SolrQueryBuilder.replaceSOLRSyntaxisCharacters(terms[i].toLowerCase()).trim() + SOLR_ANYTHING_AFTER_CHARACTER;
                    coursesQuery.append(String.format("(name:%s AND collegeId:%s)", t, collegeId)).append(SOLR_OR_STRING);

                    coursesQuery.append(String.format("(course_code:%s AND collegeId:%s)",
		                    !t.contains("\\-") ? t : t.substring(0, t.indexOf("\\-")), collegeId));

                    suburbsQuery.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s)) ", t, t));

                    tagsQuery.append(String.format("(doctype:tag AND collegeId:%s AND name:%s)", collegeId, t));

                    if (i + 1 != terms.length) {
                        coursesQuery.append(TERMS_SEPARATOR_STRING);
                        suburbsQuery.append(TERMS_SEPARATOR_STRING);
                        tagsQuery.append(TERMS_SEPARATOR_STRING);
                    }
                }
            }
            int lastOrStringIndex = coursesQuery.lastIndexOf(TERMS_SEPARATOR_STRING);
            if (lastOrStringIndex == coursesQuery.length() - TERMS_SEPARATOR_STRING.length()) {
            	//this mean that some terms not passed the validation and we need to remove last term separator
            	coursesQuery.replace(lastOrStringIndex, coursesQuery.length(), StringUtils.EMPTY);
            	lastOrStringIndex = suburbsQuery.lastIndexOf(TERMS_SEPARATOR_STRING);
            	if (lastOrStringIndex == suburbsQuery.length() - TERMS_SEPARATOR_STRING.length()) {
            		suburbsQuery.replace(lastOrStringIndex, suburbsQuery.length(), StringUtils.EMPTY);
            	}
            	lastOrStringIndex = tagsQuery.lastIndexOf(TERMS_SEPARATOR_STRING);
            	if (lastOrStringIndex == tagsQuery.length() - TERMS_SEPARATOR_STRING.length()) {
            		tagsQuery.replace(lastOrStringIndex, tagsQuery.length(), StringUtils.EMPTY);
            	}
            	
            }

            SolrDocumentList results = new SolrDocumentList();
            if (coursesQuery.length() != 0) {
            	QueryResponse coursesResults = query(new SolrQuery(coursesQuery.toString()), SolrCore.courses);
            	if (coursesResults != null && coursesResults.getResults() != null && !coursesResults.getResults().isEmpty()) {
            		results.addAll(coursesResults.getResults());
            	}
            }
            if (suburbsQuery.length() != 0) {
            	QueryResponse suburbsResults = query(new SolrQuery(suburbsQuery.toString()), SolrCore.suburbs);
            	if (suburbsResults != null && suburbsResults.getResults() != null && !suburbsResults.getResults().isEmpty()) {
            		results.addAll(suburbsResults.getResults());
            	}
            }
            if (tagsQuery.length() != 0) {
            	QueryResponse tagsResults = query(new SolrQuery(tagsQuery.toString()), SolrCore.tags);
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
            logger.debug(solrQueryToString(solrQuery));
            QueryResponse suburbs = query(solrQuery, SolrCore.suburbs);
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
