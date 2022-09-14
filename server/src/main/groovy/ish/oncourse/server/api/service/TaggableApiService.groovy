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

package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.CayenneLayer
import ish.oncourse.server.cayenne.TaggableTrait
import ish.oncourse.server.cayenne.TaggableTraitConstants
import org.apache.cayenne.Persistent

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.traits._DTOTrait
import ish.oncourse.server.cayenne.Tag

abstract class TaggableApiService <T extends _DTOTrait, K extends Persistent & TaggableTrait, M extends CayenneLayer<K>> extends EntityApiService<T, K, M> {

    Closure getAction(String key, String value) {
        Closure action = null
        switch (key) {
            case TaggableTraitConstants.BULK_TAG_PROPERTY:
                action = { K entity ->
                    def taggableEntity = entity as TaggableTrait
                    value.split(",").each { id ->
                        Tag dbTag = getRecordById(taggableEntity.context, Tag, id as Long)
                        if (!taggableEntity.hasTag(dbTag.getName(), true) && !taggableEntity.addTag(dbTag)) {
                            validator.throwClientErrorException(taggableEntity.id, key, "Selected record can’t be tagged with ${dbTag.name}")
                        }
                    }
                }
                break
            case TaggableTraitConstants.BULK_UNTAG_PROPERTY:
                action = { K entity ->
                    def taggableEntity = entity as TaggableTrait
                    value.split(",").each { id ->
                        Tag dbTag = getRecordById(taggableEntity.context, Tag, id as Long)
                        if (taggableEntity.hasTag(dbTag.getName(), true) && !taggableEntity.removeTag(dbTag)) {
                            validator.throwClientErrorException(taggableEntity.id, key, "Selected record can’t be tagged with ${dbTag.name}")
                        }
                    }
                }
                break
        }
        action
    }

}
