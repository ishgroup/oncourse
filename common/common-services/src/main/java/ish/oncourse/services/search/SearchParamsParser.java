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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    public SearchParamsParser(Request request, ISearchService searchService, ITagService tagService) {
        this.request = request;
        this.searchService = searchService;
        this.tagService = tagService;
    }

    public void parse()
    {
        Tag browseTag = null;

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
                        searchParams.setAfter(parseAfter(parameter));
                        value = searchParams.getAfter();
                        break;
                    case before:
                        searchParams.setBefore(parseAfter(parameter));
                        value = searchParams.getBefore();
                        break;
                    case directSearch:
                    	searchParams.setWithDirectSearch(Boolean.valueOf(parameter));
                    	value = searchParams.getWithDirectSearch();
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

    private Date parseAfter(String parameter) {
        try {
            return FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, null).parse(parameter);
        } catch (ParseException e) {
            return null;
        }
    }

    private Double parseKm(String parameter) {
        return  StringUtils.isNumeric(parameter) ? Double.valueOf(parameter):null;
    }

    private String parseTime(String parameter) {
        return (parameter.equalsIgnoreCase(PARAM_VALUE_daytime) || parameter.equalsIgnoreCase(PARAM_VALUE_evening)) ? parameter:null;
    }

    private Tag parseSubject(String parameter) {
        return tagService.getTagByFullPath(parameter);
    }

    private Double parsePrice(String parameter) {
        return parameter.matches(PATTERN_PRICE)? Double.valueOf(parameter.replaceAll("[$]", "")):null;
    }

    private SolrDocumentList parseNear(String parameter) {
        SolrDocumentList solrSuburbs = searchService.searchSuburb(parameter);
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
