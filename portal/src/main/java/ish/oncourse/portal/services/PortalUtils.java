package ish.oncourse.portal.services;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static ish.oncourse.util.FormatUtils.*;

public class PortalUtils {

    public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
    public static final String CONTENT_TYPE = "text/json";

    private static final String URL_CLASS_TEMPLATE = "http://%s/class/%s-%s";
    private static final String URL_COURSE_TEMPLATE = "http://%s/course/%s";

    private static final int CLASS_DETAILS_LENGTH = 490;
    private static final int COURSE_DETAILS_LENGTH = 245;
    private static final int TAG_DETAILS_LENGTH = -1;

    private static final String CLASS_INTERVAL_INFO_TEMPLATE = "%s - %s ";

    private static final String CLASS_SESSION_INFO_TEMPLATE = " %s session, %s hours";

    private static final String CLASS_SESSIONS_INFO_TEMPLATE = " %s sessions, %s hours";




    public static String getCourseDetailsURLBy(Course course, IWebSiteService webSiteService)
    {
        return course != null ? String.format(URL_COURSE_TEMPLATE, webSiteService.getCurrentDomain().getName(),
                course.getCode()) : EMPTY_STRING;
    }

    public static String getClassDetailsURLBy(CourseClass courseClass, IWebSiteService webSiteService)
    {
        return courseClass != null ? String.format(URL_CLASS_TEMPLATE, webSiteService.getCurrentDomain().getName(),
                courseClass.getCourse().getCode(),courseClass.getCode()) : EMPTY_STRING;
    }


    public static String getClassDetailsBy(CourseClass courseClass,
                                           ITextileConverter textileConverter,
                                           IPlainTextExtractor extractor)
    {
        StringBuilder textileDetails = new StringBuilder();
        if(courseClass.getDetail() != null && courseClass.getDetail().length() > 0) {
            textileDetails.append(courseClass.getDetail());
        }

        if (courseClass.getCourse().getDetail() != null &&  courseClass.getCourse().getDetail().length() > 0) {
            if(textileDetails.toString().length() < 0) {
                textileDetails.append("\n");
            }
            textileDetails.append(courseClass.getCourse().getDetail());
        }

        return stringToTextileStringBy(textileDetails.toString(),CLASS_DETAILS_LENGTH, textileConverter,extractor);
    }

    public static String getCourseDetailsBy(Course course,
                                           ITextileConverter textileConverter,
                                           IPlainTextExtractor extractor)
    {
        StringBuilder textileDetails = new StringBuilder();
        if(course.getDetail() != null && course.getDetail().length() > 0) {
            textileDetails.append(course.getDetail());
        }

        return stringToTextileStringBy(textileDetails.toString(),COURSE_DETAILS_LENGTH, textileConverter,extractor);
    }

    public static String getTagDetailsBy(Tag tag,
                                     ITextileConverter textileConverter,
                                     IPlainTextExtractor extractor)
    {
        return stringToTextileStringBy(tag.getDetail(), TAG_DETAILS_LENGTH, textileConverter,extractor);

    }

    public static String stringToTextileStringBy(String textileString, int resultStringLength,
                                            ITextileConverter textileConverter,
                                            IPlainTextExtractor extractor)
    {

        if (StringUtils.trimToNull(textileString) != null)
        {
            String details = textileConverter.convertCustomTextile(textileString, new ValidationErrors());
            details = extractor.extractFromHtml(details);
            details = resultStringLength == -1 ? details : StringUtils.abbreviate(details, resultStringLength);
            return details;
        }
        else
        {
            return EMPTY_STRING;
        }
    }


    public static String getClassIntervalInfoBy(CourseClass courseClass)
    {
        Date start = courseClass.getStartDate();
        Date end = courseClass.getEndDate();
        DateFormat startDateFormatter = getDateFormat(DATE_FORMAT_dd_MMM ,courseClass.getTimeZone());
        DateFormat endDateFormatter = getDateFormat(shortDateFormatString ,courseClass.getTimeZone());
        if (start == null && end == null) {
            return EMPTY_STRING;
        }

        return String.format(CLASS_INTERVAL_INFO_TEMPLATE, startDateFormatter.format(start),
                endDateFormatter.format(end));

    }


    public static String getClassSessionsInfoBy(CourseClass courseClass) {
        DecimalFormat hoursFormat = new DecimalFormat("0.#");
        int numberOfSession = courseClass.getSessions().size();
        String key = (numberOfSession > 1) ? CLASS_SESSIONS_INFO_TEMPLATE
                : CLASS_SESSION_INFO_TEMPLATE;
        return String.format(key, numberOfSession, hoursFormat
                .format(courseClass.getTotalDurationHours().doubleValue()));
    }

    public static String getClassPlaceBy(CourseClass courseClass)
    {
        String room = courseClass.getRoom() != null ? courseClass.getRoom().getName() : null;
        String site = courseClass.getRoom() != null ?
                courseClass.getRoom().getSite() != null ?
                        courseClass.getRoom().getSite().getName() : null : null;
        ArrayList<String> values = new ArrayList<String>();

        if (site != null)
            values.add(site);
        if (room != null)
            values.add(room);
        return StringUtils.join(values,", ");
    }



}
