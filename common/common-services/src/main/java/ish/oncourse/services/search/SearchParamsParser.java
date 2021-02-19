package ish.oncourse.services.search;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.model.College;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import ish.oncourse.services.courseclass.GetHideOnWebClassAge;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.solr.query.Duration;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.Suburb;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SearchParamsParser
{

    public static final String PARAM_VALUE_weekday = "weekday";
    public static final String PARAM_VALUE_weekend = "weekend";

    public static final String PARAM_VALUE_daytime = "daytime";
    public static final String PARAM_VALUE_evening = "evening";

    static final String DATE_FORMAT_FOR_AFTER_BEFORE = "yyyyMMdd";

    public static final String PATTERN_PRICE = "[$]?(\\d)+[$]?";

    private static final Logger logger = LogManager.getLogger();
    private ParametersProvider provider;
    private ISearchService searchService;
    private ITagService tagService;

    private Map<SearchParam, String> paramsInError = new HashMap<>();

    private SearchParams searchParams = new SearchParams();
    private TimeZone clientTimezone;
	private College college;

	public void parse() {
        Tag browseTag = null, tag = null;
        
		searchParams.setClassAge(new GetHideOnWebClassAge().college(college).get());
        searchParams.setClientTimezone(clientTimezone);
        for (SearchParam name : SearchParam.values()) {
            String parameter = StringUtils.trimToNull(provider.getParameter(name.name()));
            Object value = null;
            if (parameter != null) {
                switch (name) {
                    case s :
                        searchParams.setS(parameter);
                        value = searchParams.getS();
                        break;
                    case day:
                        searchParams.setDay(DayParser.valueOf(parameter).parse());
                        value = searchParams.getDay();
                        break;
                    case near:
                        String[] nears = provider.getParameters(SearchParam.near.name());
                        for (String near : nears) {
                            Suburb suburb = SuburbParser.valueOf(near, provider.getParameter(SearchParam.km.name()), searchService).parse();
                            if (suburb != null) {
                                searchParams.addSuburb(suburb);
                            }
                            value = suburb;
                        }
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
						String[] parameters = provider.getParameters(name.name());
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
                        value = searchParams.getSuburbs().size() > 0 ? searchParams.getSuburbs().get(0): parameter;
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
                        break;
                    case tutorId:
                        searchParams.setTutorId(parseLong(parameter));
                        value = searchParams.getTutorId();
                        break;
                    case duration:
                        String[] durations = provider.getParameters(SearchParam.duration.name());
                        for (String durationString : durations) {
                            Duration duration = Duration.valueOf(durationString);
                            if (duration != null) {
                                searchParams.addDuration(duration);
                            }
                        }
                        value = searchParams.getDurations();
                        break;
                    default:
                        logger.warn("Parser is not defined for SearchParam {}", name);
                }
            }
            if (parameter != null && value == null)
            {
                paramsInError.put(name, parameter);
            }
        }

        if (browseTag == null && provider.getParameter(SearchParam.subject.name()) == null) {
            browseTag = (Tag) provider.getAttribute(PageLinkTransformer.ATTR_coursesTag);
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
    
    public SearchParams getSearchParams() {
        return searchParams;
    }

    public Map<SearchParam, String> getParamsInError() {
        return paramsInError;
    }

    public static SearchParamsParser valueOf(final Request request,
											 College college,
											 ISearchService searchService,
                                             ITagService tagService,
                                             TimeZone clientTimezone) {
        ParametersProvider provider = new ParametersProvider() {
            @Override
            public String getParameter(String name) {
                return request.getParameter(name);
            }

            @Override
            public String[] getParameters(String name) {
                return request.getParameters(name);
            }

            @Override
            public Object getAttribute(String name) {
                return request.getAttribute(name);
            }
        };
        return valueOf(provider, college, searchService, tagService, clientTimezone);
    }


    public static SearchParamsParser valueOf(ParametersProvider provider,
											 College college,
											 ISearchService searchService,
                                             ITagService tagService,
                                             TimeZone clientTimezone) {
        SearchParamsParser result = new SearchParamsParser();
        result.college = college;
        result.provider = provider;
        result.searchService = searchService;
        result.tagService = tagService;
        result.clientTimezone = clientTimezone;
        return result;
    }

    public interface ParametersProvider {
        String getParameter(String name);
        String[] getParameters(String name);
        Object getAttribute(String name);
    }

}
