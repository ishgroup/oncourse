package ish.oncourse.portal.access.validate;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Document;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;

import java.util.List;

/**
 * Created by akoiro on 5/04/2016.
 */
public class CourseClassAccessLinksValidator implements AccessLinksValidator {
    private String path;
    private CourseClass courseClass;
    private IPortalService portalService;
    private IBinaryDataService binaryDataService;


    public boolean validate() {
        List<Document> documents = portalService.getResourcesBy(courseClass);
        for (Document document : documents) {
            if (binaryDataService.getUrl(document).endsWith(path)) {
                return true;
            }
        }
        return false;
    }

    public static CourseClassAccessLinksValidator valueOf(String path,
                                                          CourseClass courseClass,
                                                          IPortalService portalService,
                                                          IBinaryDataService binaryDataService) {
        CourseClassAccessLinksValidator result = new CourseClassAccessLinksValidator();
        result.path = path;
        result.courseClass = courseClass;
        result.portalService = portalService;
        result.binaryDataService = binaryDataService;
        return result;
    }
}
