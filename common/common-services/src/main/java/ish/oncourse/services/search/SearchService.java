package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.model.SearchParam;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SearchService implements ISearchService {

    private static final Logger logger = Logger.getLogger(SearchService.class);

    /**
     * Default maximum distance for geo-searches
     */
    static final int MAX_DISTANCE = 100;

    private static final String DATE_BOOST_STM = "{!boost b=$dateboost v=$qq}";

    private static final String DATE_BOOST_FUNCTION = "recip(max(ms(startDate, NOW), 0),1.15e-8,1,1)";

    private static final String FILTER_TEMPLATE = "+collegeId:%s +doctype:course end:[NOW TO *]";
    private static final String FILTER_TEMPLATE_FOR_PARAM_s = " (detail: %s || tutor:%s || course_code:%s) ";

    private static final String FILTER_TEMPLATE_price = " price:[* TO %s] ";
    private static final String FILTER_TEMPLATE_when = " when:%s ";
    private static final String FILTER_TEMPLATE_tagId = "tagId:%d";
    private static final String FILTER_TEMPLATE_brackets = "(%s)";


    private static final String FILTER_TEMPLATE_AND = "AND";
    private static final String FILTER_TEMPLATE_OR = "||";
    private static final String FILTER_TEMPLATE_ALL = "(*:*)";


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
    private QueryResponse query(SolrQuery q) throws SolrServerException {
        int count = 0;
        SolrServerException exception = null;
        while (count < 3) {
            try {
                return getSolrServer(SolrCore.courses).query(q);
            } catch (SolrServerException e) {
                exception = e;
                count++;
                handleException(null,e,q,count);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
            }
        }
        if (exception!= null)
            throw exception;
        else
            throw new IllegalArgumentException();
    }

    /**
     * The method logs stacktraces every exception from hierarchy. I have added it to see full stack trace of a exception.
     */
    private void handleException(Throwable parent, Throwable current, SolrQuery solrQuery, int count)
    {
        if (parent == null)
            logger.error(String.format("Cannot execute query: %s with attempt %d",solrQueryToString(solrQuery),count), current);
        else
            logger.error(String.format("Cause of parent %s. Cannot execute query: %s with attempt %d",parent.getClass().getName(),solrQueryToString(solrQuery),count), current);

        if (current.getCause() != null)
            handleException(current, current.getCause(), solrQuery,count);
    }

    public QueryResponse searchCourses(Map<SearchParam, Object> params, int start, int rows) {

        try {

            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
            SolrQuery q = new SolrQueryBuilder(this, params, collegeId, start, rows).create();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Solr query:%s", solrQueryToString(q)));
            }

            return query(q);

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
            term = ClientUtils.escapeQueryChars(term);
            College college = webSiteService.getCurrentCollege();
            String collegeId = String.valueOf(college.getId());

            StringBuilder coursesQuery = new StringBuilder();
            StringBuilder suburbsQuery = new StringBuilder();
            StringBuilder tagsQuery = new StringBuilder();

            String[] terms = term.split("[\\s]+");
            for (int i = 0; i < terms.length; i++) {
                if (!terms[i].equals("")) {
                    String t = terms[i].toLowerCase().trim() + "*";
                    coursesQuery.append(String.format("(name:%s AND collegeId:%s)", t, collegeId)).append("||");

                    coursesQuery.append(String.format("(course_code:%s AND collegeId:%s)",
                            t.indexOf("\\-") < 0 ? t : t.substring(0, t.indexOf("\\-")), collegeId));

                    suburbsQuery.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s)) ", t, t));

                    tagsQuery.append(String.format("(doctype:tag AND collegeId:%s AND name:%s)", collegeId, t));

                    if (i + 1 != terms.length) {
                        coursesQuery.append(" || ");
                        suburbsQuery.append(" || ");
                        tagsQuery.append(" || ");
                    }
                }
            }

            SolrDocumentList results = new SolrDocumentList();
            if (coursesQuery.length() != 0) {
                results.addAll(getSolrServer(SolrCore.courses).query(new SolrQuery(coursesQuery.toString())).getResults());
            }
            if (suburbsQuery.length() != 0) {
                results.addAll(getSolrServer(SolrCore.suburbs).query(new SolrQuery(suburbsQuery.toString())).getResults());
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
            term = ClientUtils.escapeQueryChars(term);
            SolrQuery q = new SolrQuery();

            StringBuilder query = new StringBuilder();

            String[] terms = term.split("[\\s]+");
            for (int i = 0; i < terms.length; i++) {
                if (!terms[i].equals("")) {
                    String t = terms[i].toLowerCase().trim() + "*";

                    query.append(String.format("(doctype:suburb AND (suburb:%s || postcode:%s)) ", t, t));

                    if (i + 1 != terms.length) {
                        query.append(" || ");
                    }
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

            String[] suburbParams = separator > 0 ? new String[]{location.substring(0, separator), location.substring(separator + 1)}
                    : new String[]{location, null};
            if (suburbParams[1] != null && !suburbParams[1].matches("(\\d)+")) {
                suburbParams[0] = location;
                suburbParams[1] = null;
            }
            SolrQuery q = new SolrQuery();

            StringBuilder query = new StringBuilder();
            query.append("(doctype:suburb");
            if (suburbParams[0] != null) {
                String near = suburbParams[0].replaceAll("[\\s]+", "+");
                query.append(" AND (suburb:").append(near);
                query.append("|| postcode:").append(near);
                query.append(")");
            }

            if (suburbParams[1] != null) {
                query.append(" AND postcode:").append(suburbParams[1]);
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
