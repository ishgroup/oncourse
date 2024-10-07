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

package ish.oncourse.server.api.dao


import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRelation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.v1.function.TagRequirementFunctions.getTaggableClassForName

class TagDao implements CayenneLayer<Tag> {

    @Override
    Tag newObject(ObjectContext context) {
        context.newObject(Tag)
    }

    @Override
    Tag getById(ObjectContext context, Long id) {
        SelectById.query(Tag, id)
                .selectOne(context)
    }

    List<Tag> getRelatedFor(ObjectContext context, Queueable obj) {
        getRelatedForQueueableId(context, obj.class.simpleName, obj.id)
    }

    List<Tag> getRelatedForQueueableId(ObjectContext context, String entityName, Long id) {
        def taggableClases = getTaggableClassForName(entityName).databaseValue
        ObjectSelect
                .query(Tag)
                .where(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_ANGEL_ID_PROPERTY).eq(id)
                        .andExp(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_IDENTIFIER).eq(taggableClases)))
                .select(context)
    }
}
