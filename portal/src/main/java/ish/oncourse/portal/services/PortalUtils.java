package ish.oncourse.portal.services;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;

public class PortalUtils {

    public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
    public static final String CONTENT_TYPE = "text/json";

    private static final String URL_CLASS_TEMPLATE = "http://%s/class/%s-%s";
    private static final String URL_COURSE_TEMPLATE = "http://%s/course/%s";
    public static final String EMPTY_STRING = "";

    private static final int CLASS_DETAILS_LENGTH = 490;
    private static final int COURSE_DETAILS_LENGTH = 245;




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

        String details = textileConverter.convertCustomTextile(textileDetails.toString(), new ValidationErrors());
        details = extractor.extractFromHtml(details);
        details = StringUtils.abbreviate(details, CLASS_DETAILS_LENGTH);
        return details;
    }

    public static String getCourseDetailsBy(Course course,
                                           ITextileConverter textileConverter,
                                           IPlainTextExtractor extractor)
    {
        StringBuilder textileDetails = new StringBuilder();
        if(course.getDetail() != null && course.getDetail().length() > 0) {
            textileDetails.append(course.getDetail());
        }

        String details = textileConverter.convertCustomTextile(textileDetails.toString(), new ValidationErrors());
        details = extractor.extractFromHtml(details);
        details = StringUtils.abbreviate(details, COURSE_DETAILS_LENGTH);
        return details;
    }


}
