package ish.oncourse.services.search;

import ish.oncourse.model.Course;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.services.Request;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class SearchParamsParser
{

    public static final String PARAM_VALUE_weekday = "weekday";
    public static final String PARAM_VALUE_weekend = "weekend";

    public static final String PARAM_VALUE_daytime = "daytime";
    public static final String PARAM_VALUE_evening = "evening";

    static final String DATE_FORMAT_FOR_AFTER_BEFORE = "yyyyMMdd";

    public static final String PATTERN_PRICE = "[$]?(\\d)+[$]?";

    private static final Logger LOGGER = Logger.getLogger(SearchParamsParser.class);
    private final Request request;
    private final ISearchService searchService;
    private ITagService tagService;

    private Map<SearchParam, String> paramsInError = new HashMap<SearchParam, String>();

    private SearchParams searchParams = new SearchParams();
    private TimeZone clientTimezone;

    public SearchParamsParser(Request request, ISearchService searchService, ITagService tagService) {
    	this(request, searchService, tagService, null);
    }
        
	public SearchParamsParser(Request request, ISearchService searchService, ITagService tagService, TimeZone clientTimezone) {
		this.request = request;
		this.searchService = searchService;
		this.tagService = tagService;
		this.clientTimezone = clientTimezone;
	}

	public void parse() {
        Tag browseTag = null;
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
                    case time:
                        searchParams.setTime(parseTime(parameter));
                        value = searchParams.getTime();
                        break;
                    case km:
                        searchParams.setKm(parseKm(parameter));
                        value = searchParams.getKm();
                        break;
                    case after:
                        searchParams.setAfter(parseDate(parameter, SearchParam.after));
                        value = searchParams.getAfter();
                        break;
                    case before:
                        searchParams.setBefore(parseDate(parameter, SearchParam.before));
                        value = searchParams.getBefore();
                        break;
                    case debugQuery:
                    	searchParams.setDebugQuery(Boolean.valueOf(parameter));
                    	value = searchParams.getDebugQuery();
                    	break;
                    default:
                        LOGGER.warn(String.format("Parser is not defined for SearchParam \"%s\"", name));
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

    private Date parseDate(String parameter, SearchParam paramName) {
        try {
        	if (clientTimezone == null) {
        		Date parsedDate = FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, FormatUtils.TIME_ZONE_UTC).parse(parameter);
        		return parsedDate;
        	} else if (clientTimezone instanceof SimpleTimeZone) {
        		Date parsedDate = FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, FormatUtils.TIME_ZONE_UTC).parse(parameter);
            	Calendar calendar = Calendar.getInstance();
            	calendar.setTime(parsedDate);
            	calendar.add(Calendar.MILLISECOND, -clientTimezone.getRawOffset());
            	return calendar.getTime();
        	} else if (clientTimezone instanceof TimeZone) {
        		Date parsedDate = FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, clientTimezone).parse(parameter);
        		return parsedDate;
        	} else {
        		LOGGER.error(String.format("Unexpected client timezone param %s", clientTimezone));
        		paramsInError.put(paramName, parameter);
        		return null;
        	}
        } catch (ParseException e) {
            return null;
        }
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

    private String parseTime(String parameter) {
        return (parameter.equalsIgnoreCase(PARAM_VALUE_daytime) || parameter.equalsIgnoreCase(PARAM_VALUE_evening)) ? parameter:null;
    }

    private Tag parseSubject(String parameter) {
        return tagService.getTagByFullPath(parameter);
    }

    private Double parsePrice(String parameter) {
        return parameter.matches(PATTERN_PRICE) ? Double.valueOf(parameter.replaceAll("[$]", StringUtils.EMPTY)) : null;
    }
    
    public static String convertPostcodeParameterToLong(String parameter) {
    	//the workaround is for #17051. Till postcode stored as the long in db and indexed as is we need to call String-to-Long and back conversion 
    	//to be able found the postcodes which starts from 0
    	if (StringUtils.isNumeric(parameter)) {
    		parameter = Long.valueOf(parameter).toString();
    	}
    	return parameter;
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
}
