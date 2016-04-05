package ish.oncourse.portal.access.validate;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by akoiro on 5/04/2016.
 */
public class AccessLinksValidatorFactory {
    private static final DefaultAccessLinksValidator defaultAccessLinksValidator = new DefaultAccessLinksValidator();

    private static final String COURSE_CLASS_PAGE_REGEXP = "/class/([0-9]{1,})";

    @Inject
    private IPortalService portalService;

    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private ICayenneService cayenneService;

    public AccessLinksValidator getBy(String prevPath, String currentPath) {
        if (prevPath.matches(COURSE_CLASS_PAGE_REGEXP)) {
            AccessLinksValidator courseClass = getCourseClassAccessLinksValidator(prevPath, currentPath);
            if (courseClass != null) return courseClass;

        }
        return defaultAccessLinksValidator;
    }

    private AccessLinksValidator getCourseClassAccessLinksValidator(String prevPath, String currentPath) {
        Pattern pattern = Pattern.compile(COURSE_CLASS_PAGE_REGEXP);
        Matcher matcher = pattern.matcher(prevPath);
        if (matcher.matches()) {
            String id = matcher.group(1);
            if (StringUtils.isNumeric(id)) {
                CourseClass courseClass = SelectById.query(CourseClass.class, Long.valueOf(id)).selectOne(cayenneService.newContext());
                return CourseClassAccessLinksValidator.valueOf(currentPath, courseClass, portalService, binaryDataService);
            }
        }
        return defaultAccessLinksValidator;
    }

    private static class DefaultAccessLinksValidator implements AccessLinksValidator {
        @Override
        public boolean validate() {
            return false;
        }
    }

    public static AccessLinksValidatorFactory valueOf(IPortalService portalService, IBinaryDataService binaryDataService, ICayenneService cayenneService) {
        AccessLinksValidatorFactory factory = new AccessLinksValidatorFactory();
        factory.portalService = portalService;
        factory.binaryDataService = binaryDataService;
        factory.cayenneService = cayenneService;
        return factory;
    }
}
