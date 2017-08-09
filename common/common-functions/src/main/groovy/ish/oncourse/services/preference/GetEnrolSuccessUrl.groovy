package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.persistence.CommonPreferenceController
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils


class GetEnrolSuccessUrl extends GetPreference {
    
    GetEnrolSuccessUrl(College college,  ObjectContext objectContext) {
        super(college, CommonPreferenceController.COLLEGE_ENROL_SUCCESS_URL, objectContext)
    }

    String get() {
        String url = StringUtils.trimToNull(getValue())

        if (url && !new URI(url).isAbsolute() && !url.startsWith("/")) {
            return  "/$url"
        }
        
        return url?: "/Courses"
    }
    
}
