package ish.oncourse.portal.util;

import ish.oncourse.model.College;
import ish.oncourse.model.CustomFieldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by pavel on 8/1/17.
 */
public class GetCustomFieldTypeByKey {

    private static Logger logger = LogManager.getLogger();

    private College college;

    public static GetCustomFieldTypeByKey valueOf(College college) {
        GetCustomFieldTypeByKey container = new GetCustomFieldTypeByKey();
        container.college = college;
        return container;
    }

    public CustomFieldType get(String key) {
        CustomFieldType res = null;
        for (CustomFieldType fieldType : college.getCustomFieldTypes()) {
            if (fieldType.getKey().equals(key)) {
                res = fieldType;
            }
        }
        if (res == null) {
            logger.error("Custom field with key '{}' not found on college id : {}", key, college.getId());
        }

        return res;
    }
}
