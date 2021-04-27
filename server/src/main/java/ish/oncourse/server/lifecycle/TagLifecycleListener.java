/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.server.cayenne.Field;
import ish.oncourse.server.cayenne.Tag;
import org.apache.cayenne.annotation.PreRemove;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.query.ObjectSelect;


/**
 * The tags can be included to data collection forms.
 * But not by primary key and by tag name (some analogy of CustomField.fieldKey). 
 * So, to keep the tags->forms in sync we  need to track tag name changes and also tag ‘removal’ event
 */
public class TagLifecycleListener {

    private final static String PROPERTY_FORMAT = "%s%s";

    public TagLifecycleListener() {}

    @PreUpdate(Tag.class)
    public void postAdd(Tag obj) {
        if (obj.getParentTag() == null && obj.getIsVocabulary()) {
            var context = obj.getContext();

            var nameChangeHandler = new TagNameChangeHandler(context);
            ChangeFilter.preCommitGraphDiff(context).apply(nameChangeHandler);

            var relatedFields = ObjectSelect
                    .query(Field.class)
                    .where(Field.PROPERTY.eq(getTagProperty(nameChangeHandler.getOldValueFor(obj.getObjectId()))))
                    .select(context);

            if (!relatedFields.isEmpty()) {
                relatedFields.forEach(field ->
                        field.setProperty(getTagProperty(nameChangeHandler.getNewValueFor(obj.getObjectId()))));
            }
        }
    }

    @PreRemove(Tag.class)
    public void preRemove(Tag obj) {
        if (obj.getParentTag() == null && obj.getIsVocabulary()) {
            var context = obj.getContext();

            var relatedFields = ObjectSelect
                    .query(Field.class)
                    .where(Field.PROPERTY.eq(getTagProperty(obj.getName())))
                    .select(context);

            if (!relatedFields.isEmpty()) {
                context.deleteObjects(relatedFields);
            }
        }
    }

    private String getTagProperty(String tagName) {
        return String.format(PROPERTY_FORMAT,
                PropertyGetSetFactory.TAG_PATTERN,
                tagName);
    }
}
