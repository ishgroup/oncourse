package ish.oncourse.services.search;

import ish.oncourse.model.College;
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
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
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
    public static final int MAX_DISTANCE = 100;

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
    private SolrDocumentList query(SolrQuery q, SolrCore core) throws SolrServerException {
        int count = 0;
        SolrServerException exception = null;
        while (count < 3) {
            try {
                return getSolrServer(core).query(q).getResults();
            } catch (SolrServerException e) {
                exception = e;
                count++;
                SolrDocumentList result = handleException(e,q,count);
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
    private SolrDocumentList handleException(Throwable throwable, SolrQuery solrQuery, int count)
    {
        logger.warn(String.format("Cannot execute query: %s with attempt %d",solrQueryToString(solrQuery),count), throwable);

        if (throwable instanceof SolrServerException &&
                throwable.getCause() instanceof SolrException &&
                throwable.getCause().getMessage().contains("Bad Request"))
        {
            return new SolrDocumentList();
        }
        else
            return null;
    }

    public SolrDocumentList searchCourses(SearchParams params, int start, int rows) {

        try {

            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());
            SolrQuery q = new SolrQueryBuilder(params, collegeId, start, rows).create();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Solr query:%s", solrQueryToString(q)));
            }

            return query(q,SolrCore.courses);

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

            String[] terms = term.split("[\\s]+");
            for (int i = 0; i < terms.length; i++) {
                if (!terms[i].equals("")) {
                	//add escape for characters which part of the SOLR syntax and need to be escaped to avoid exception
                    String t = escapeQueryChars(terms[i].toLowerCase().trim()) + "*";
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
                results.addAll(query(new SolrQuery(coursesQuery.toString()), SolrCore.courses));
            }
            if (suburbsQuery.length() != 0) {
                results.addAll(query(new SolrQuery(suburbsQuery.toString()), SolrCore.suburbs));
            }
            if (tagsQuery.length() != 0) {
                results.addAll(query(new SolrQuery(tagsQuery.toString()), SolrCore.tags));
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
            return query(q, SolrCore.suburbs);

        } catch (Exception e) {
            logger.error("Failed to search suburbs.", e);
            throw new SearchException("Unable to find suburbs.", e);
        }
    }
    
    private static String escapeQueryChars(String original) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < original.length(); i++) {
    		char c = original.charAt(i);
    		// These characters are part of the SOLR query syntax and must be escaped
    		if (c == '\\' || c == '+' || c == '-' || c == '!'  || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' 
    			|| c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&'  || c == ';') {
    			sb.append('\\');
    		}
    		sb.append(c);
    	}
    	return sb.toString();
    }

    public SolrDocumentList searchSuburb(String location) {
        try {
        	location = location.trim();
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
                query.append(" || postcode:").append(near);
                query.append(")");
            }

            if (suburbParams[1] != null) {
                query.append(" AND postcode:").append(suburbParams[1]);
            }

            query.append(") ");

            q.setQuery(query.toString());

            return query(q, SolrCore.suburbs);
        } catch (Exception e) {
            logger.error("Failed to search suburb.", e);
            throw new SearchException("Unable to find suburb.", e);
        }
    }

    enum SolrCore {
        courses, suburbs, tags
    }
}
