package ish.oncourse.services.search;

import ish.oncourse.model.Course;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.services.Request;

import java.text.ParseException;
import java.util.*;

public class SearchParamsParser
{

    public static final String PARAM_VALUE_weekday = "weekday";
    public static final String PARAM_VALUE_weekend = "weekend";

    public static final String PARAM_VALUE_daytime = "daytime";
    public static final String PARAM_VALUE_evening = "evening";

    static final String DATE_FORMAT_FOR_AFTER_BEFORE = "yyyyMMdd";

    public static final String PATTERN_PRICE = "[$]?(\\d)+[$]?";

    private static final Logger logger = LogManager.getLogger();
    private Request request;
    private ISearchService searchService;
    private ITagService tagService;

    private Map<SearchParam, String> paramsInError = new HashMap<>();

    private SearchParams searchParams = new SearchParams();
    private TimeZone clientTimezone;

	public void parse() {
        Tag browseTag = null, tag = null;

        searchParams.setClientTimezone(clientTimezone);
        for (SearchParam name : SearchParam.values()) {
            String parameter = StringUtils.trimToNull(request.getParameter(name.name()));
            Object value = null;
            if (parameter != null) {
                switch (name) {
                    case s :
                        searchParams.setS(parameter);
                        value = searchParams.getS();
                        break;
                    case day:
                        searchParams.setDay(parseDay(parameter));
                        value = searchParams.getDay();
                        break;
                    case near:
                        searchParams.setNear(parseNear(parameter));
                        value = searchParams.getNear();
                        break;
                    case price:
                        searchParams.setPrice(parsePrice(parameter));
                        value = searchParams.getPrice();
                        break;
                    case subject:
                        browseTag = parseSubject(parameter);
                        searchParams.setSubject(browseTag);
                        value = searchParams.getSubject();
                        break;
					case tag:
						String[] parameters = request.getParameters(name.name());
						for (String tagParameter : parameters) {
							tag = parseSubject(tagParameter);
							if (tag != null) {
								searchParams.addTag(tag);
							}
						}
						value = tag;
						break;
                    case time:
                        searchParams.setTime(parseTime(parameter));
                        value = searchParams.getTime();
                        break;
                    case km:
                        searchParams.setKm(parseKm(parameter));
                        value = searchParams.getKm();
                        break;
                    case after:
                        searchParams.setAfter(DateParser.valueOf(parameter, clientTimezone).parse());
                        value = searchParams.getAfter();
                        break;
                    case before:
                        searchParams.setBefore(DateParser.valueOf(parameter, clientTimezone).parse());
                        value = searchParams.getBefore();
                        break;
                    case debugQuery:
                    	searchParams.setDebugQuery(Boolean.valueOf(parameter));
                    	value = searchParams.getDebugQuery();
                    	break;
                    case site:
                        searchParams.setSiteId(parseLong(parameter));
                        value = searchParams.getSiteId();
                    default:
                        logger.warn("Parser is not defined for SearchParam {}", name);
                }
            }
            if (parameter != null && value == null)
            {
                paramsInError.put(name, parameter);
            }
        }

        if (browseTag == null && request.getParameter(SearchParam.subject.name()) == null) {
            browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
            if (browseTag != null) {
                //this code updated because getDefaultPath() return incorrect value for tag group which have aliases
                searchParams.setSubject(browseTag);
            }
        }
    }

    private Long parseLong(String parameter) {
        if (StringUtils.isNumeric(parameter)) {
            return Long.valueOf(parameter);
        }
        return null;
    }

    private String parseTime(String parameter) {
        return (parameter.equalsIgnoreCase(PARAM_VALUE_daytime) || parameter.equalsIgnoreCase(PARAM_VALUE_evening)) ? parameter:null;
    }

    private Tag parseSubject(String parameter) {
        return tagService.getTagByFullPath(parameter);
    }

    private Double parsePrice(String parameter) {
        return parameter.matches(PATTERN_PRICE) ? Double.valueOf(parameter.replaceAll("[$]", StringUtils.EMPTY)) : null;
    }
    
    private SolrDocumentList parseNear(String parameter) {
        SolrDocumentList solrSuburbs = searchService.searchSuburb(convertPostcodeParameterToLong(parameter));
        return solrSuburbs != null && !solrSuburbs.isEmpty() ? solrSuburbs:null;
    }

    private String parseDay(String parameter) {
        return parameter.equalsIgnoreCase(PARAM_VALUE_weekday) || parameter.equalsIgnoreCase(PARAM_VALUE_weekend)?parameter:null;
    }

    public SearchParams getSearchParams() {
        return searchParams;
    }

    public Map<SearchParam, String> getParamsInError() {
        return paramsInError;
    }

    public static SearchParamsParser valueOf(Request request,
                                             ISearchService searchService,
                                             ITagService tagService,
                                             TimeZone clientTimezone) {
        SearchParamsParser result = new SearchParamsParser();
        result.request = request;
        result.searchService = searchService;
        result.tagService = tagService;
        result.clientTimezone = clientTimezone;
        return result;
    }

    public static Double parseKm(String parameter) {
        if (StringUtils.isNumeric(parameter)) {
            Double km = Double.valueOf(parameter);
            if (km != null) {
                if (SearchService.MAX_DISTANCE < km) {
                    //check for higher distance
                    km = SearchService.MAX_DISTANCE;
                } else if (km < SearchService.MIN_DISTANCE) {
                    //check for lower distance
                    km = SearchService.MIN_DISTANCE;
                }
            }
            return km;
        }
        return null;
    }

    public static String convertPostcodeParameterToLong(String parameter) {
        //the workaround is for #17051. Till postcode stored as the long in db and indexed as is we need to call String-to-Long and back conversion
        //to be able found the postcodes which starts from 0
        if (StringUtils.isNumeric(parameter)) {
            parameter = Long.valueOf(parameter).toString();
        }
        return parameter;
    }


}
