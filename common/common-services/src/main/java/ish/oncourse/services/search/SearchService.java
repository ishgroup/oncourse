package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchService implements ISearchService {

    private static final String SOLR_DOCUMENT_NAME_FIELD = "name";

	private static final String SOLR_DOCUMENT_ID_FIELD = "id";

	private static final String SOLR_DOCUMENT_SCORE_FIELD = "score";

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
    
    @Inject
	private ICayenneService cayenneService;

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
    
    private SolrDocumentList courseQueryWithAddDirectSearch(SolrQuery q, SolrCore core, String term, Long collegeId, boolean fromAutoSuggest) throws SolrServerException{
    	try {
    		//call SOLR first
			SolrDocumentList originalResult = query(q, core);
			SelectQuery courseQuery = new SelectQuery(Course.class, ExpressionFactory.likeIgnoreCaseExp(Course.NAME_PROPERTY, "%"+term+"%")
				.andExp(ExpressionFactory.matchDbExp(Course.COLLEGE_PROPERTY + "." + College.ID_PK_COLUMN, collegeId)));
			@SuppressWarnings("unchecked")
			List<Course> directCourses = cayenneService.sharedContext().performQuery(courseQuery);
			//start to check direct results if exist
			if (!directCourses.isEmpty()) {
				if (fromAutoSuggest) {
					//TODO: implement me
				} else {
					Float maxScore = null;
					for (Course course : directCourses) {
						boolean existInResult = false;
						SolrDocument document = null;
						for (Iterator<SolrDocument> iterator = originalResult.iterator(); iterator.hasNext(); ) {
							document = iterator.next();
							if (maxScore == null) {
								maxScore = (Float) document.getFieldValue(SOLR_DOCUMENT_SCORE_FIELD);
							}
							Long courseId = Long.valueOf((String) document.getFieldValue(SOLR_DOCUMENT_ID_FIELD));
							if (course.getId().equals(courseId)) {
								existInResult = true;
								break;
							}
						}
						if (!existInResult) {
							document = new SolrDocument();
							document.setField(SOLR_DOCUMENT_ID_FIELD, course.getId().toString());
							if (maxScore == null) {
								maxScore = 0f;
							}
							document.setField(SOLR_DOCUMENT_NAME_FIELD, course.getName());
							document.setField(SOLR_DOCUMENT_SCORE_FIELD, maxScore + 10);
							originalResult.add(document);
						} else {
							Float previousScore = (Float) document.getFieldValue(SOLR_DOCUMENT_SCORE_FIELD);
							if (previousScore == null) {
								previousScore = 0f;
							}
							document.setField(SOLR_DOCUMENT_SCORE_FIELD, previousScore + 10);
						}
					}
					Collections.sort(originalResult, new Comparator<SolrDocument>() {
						@Override
						public int compare(SolrDocument document0, SolrDocument document1) {
							Float score1 = (Float) document0.getFieldValue(SOLR_DOCUMENT_SCORE_FIELD), 
								score2 = (Float) document1.getFieldValue(SOLR_DOCUMENT_SCORE_FIELD);
							if (score1 != null && score2 != null) {
								return score1.compareTo(score2);
							} else {
								logger.warn(String.format("Unable to compare documents with scores %s %s", score1, score2));
								return 0;
							}
						}
					});
				}
			}
			return originalResult;
		} catch (SolrServerException e) {
			throw e;
		}
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
            if (params.getWithDirectSearch()) {
            	return courseQueryWithAddDirectSearch(q, SolrCore.courses, params.getS(), Long.valueOf(collegeId), false);
            } else {
            	return query(q, SolrCore.courses);
            }
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

    public SolrDocumentList autoSuggest(String term, String withDirectSearch) {
    	try {
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
            	if (Boolean.valueOf(withDirectSearch)) {
            		results.addAll(courseQueryWithAddDirectSearch(new SolrQuery(coursesQuery.toString()), SolrCore.courses, term, college.getId(), true));
            	} else {
            		results.addAll(query(new SolrQuery(coursesQuery.toString()), SolrCore.courses));
            	}
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
    
    /**
     * Deprecated use {@link SearchService#autoSuggest(String, String)} instead
     */
    @Deprecated
    public SolrDocumentList autoSuggest(String term) {
        return autoSuggest(term, Boolean.FALSE.toString());
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
    
    @Deprecated
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
