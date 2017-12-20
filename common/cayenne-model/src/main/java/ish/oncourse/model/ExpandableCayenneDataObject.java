/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto.WillowCayenneObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public abstract class ExpandableCayenneDataObject extends WillowCayenneObject implements IExpandable {

    public abstract College getCollege();
    
    public abstract void setCustomFieldValue(String key, String value);

    protected CustomField getCustomField(String key) {
        for (CustomField customField : (List<CustomField>) getCustomFields()) {
            String customFieldKey = customField.getCustomFieldType().getKey();
            if (customFieldKey != null && customFieldKey .equalsIgnoreCase(key)) {
                return customField;
            }
        }
        return null;
    }

    protected  <T extends CustomField> void setCustomFieldValue(String key, String value, Class<T> type){
        CustomField field = getCustomField(key);
        if (field != null) {
            field.setValue(value);
        } else {
            ObjectContext context = getObjectContext();
            CustomFieldType customFieldType = ObjectSelect.query(CustomFieldType.class)
                    .where(CustomFieldType.COLLEGE.eq(getCollege()))
                    .and(CustomFieldType.KEY.eq(key))
                    .and(CustomFieldType.ENTITY_NAME.eq(getClass().getSimpleName()))
                    .selectFirst(context);

            if (customFieldType == null) {
                return;
            }
            T customField = context.newObject(type);
            customField.setValue(value);
            customField.setRelatedObject(this);
            customField.setCustomFieldType(customFieldType);
            customField.setCollege(getCollege());
        }
    }
}
