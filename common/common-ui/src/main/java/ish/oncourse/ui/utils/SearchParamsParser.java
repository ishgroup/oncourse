package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import ish.oncourse.services.search.ISearchService;
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
    private static final String DATE_FORMAT_FOR_AFTER_BEFORE = "yyyyMMdd";

    public static final String PARAM_VALUE_weekday = "weekday";
    public static final String PARAM_VALUE_weekend = "weekend";

    public static final String PARAM_VALUE_daytime = "daytime";
    public static final String PARAM_VALUE_evening = "evening";

    public static final String PATTERN_PRICE = "[$]?(\\d)+[$]?";

    private static final Logger LOGGER = Logger.getLogger(SearchParamsParser.class);
    private final Request request;
    private final ISearchService searchService;
    private ITagService tagService;

    private Map<SearchParam, Object> searchParams = new HashMap<SearchParam, Object>();

    private Map<SearchParam, String> paramsInError = new HashMap<SearchParam, String>();


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
                        value = parameter;
                        break;
                    case day:
                        value = parseDay(parameter);
                        break;
                    case near:
                        value = parseNear(parameter);
                        break;
                    case price:
                        value = parsePrice(parameter);
                        break;
                    case subject:
                        browseTag = parseSubject(parameter);
                        value = browseTag;
                        break;
                    case time:
                        value = parseTime(parameter);
                        break;
                    case km:
                        value = parseKm(parameter);
                        break;
                    case after:
                    case before:
                        value = parseAfter(parameter);
                        break;
                    default:
                        LOGGER.warn(String.format("Parser is not defined for SearchParam \"%s\"", name));
                }
            }

            if (value != null)
            {
                searchParams.put(name, value);
            }
            else if (parameter != null)
            {
                paramsInError.put(name, parameter);
            }
        }

        if (browseTag == null && request.getParameter(SearchParam.subject.name()) == null) {
            browseTag = (Tag) request.getAttribute(Course.COURSE_TAG);
            if (browseTag != null) {
                //this code updated because getDefaultPath() return incorrect value for tag group which have aliases
                searchParams.put(SearchParam.subject, browseTag);
            }
        }
    }

    private String parseAfter(String parameter) {
        try {
            Date date = FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, null).parse(parameter);
            return FormatUtils.convertDateToISO8601(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private String parseKm(String parameter) {
        return  StringUtils.isNumeric(parameter) ? parameter:null;
    }

    private String parseTime(String parameter) {
        return (parameter.equalsIgnoreCase(PARAM_VALUE_daytime) || parameter.equalsIgnoreCase(PARAM_VALUE_evening)) ? parameter:null;
    }

    private Tag parseSubject(String parameter) {
        return tagService.getTagByFullPath(parameter);
    }

    private String parsePrice(String parameter) {
        return parameter.matches(PATTERN_PRICE)? parameter:null;
    }

    private SolrDocumentList parseNear(String parameter) {
        SolrDocumentList solrSuburbs = searchService.searchSuburb(parameter).getResults();
        return solrSuburbs != null && !solrSuburbs.isEmpty() ? solrSuburbs:null;
    }

    private String parseDay(String parameter) {
        return parameter.equalsIgnoreCase(PARAM_VALUE_weekday) || parameter.equalsIgnoreCase(PARAM_VALUE_weekend)?parameter:null;
    }

    public Map<SearchParam, Object> getSearchParams() {
        return searchParams;
    }

    public Map<SearchParam, String> getParamsInError() {
        return paramsInError;
    }
}
