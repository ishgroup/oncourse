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
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.traits._DTOTrait
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject

abstract class TaggableApiService <T extends _DTOTrait, K extends TaggableCayenneDataObject, M extends CayenneLayer<K>> extends EntityApiService<T, K, M> {

    Closure getAction(String key, String value) {
        Closure action = null
        switch (key) {
            case TaggableCayenneDataObject.BULK_TAG_PROPERTY:
                action = { K entity ->
                    value.split(",").each { id ->
                        Tag dbTag = getRecordById(entity.context, Tag, id as Long)
                        if (!entity.hasTag(dbTag.getName(), true) && !entity.addTag(dbTag)) {
                            validator.throwClientErrorException(entity.id, key, "Selected record can’t be tagged with ${dbTag.name}")
                        }
                    }
                }
                break
            case TaggableCayenneDataObject.BULK_UNTAG_PROPERTY:
                action = { K entity ->
                    value.split(",").each { id ->
                        Tag dbTag = getRecordById(entity.context, Tag, id as Long)
                        if (entity.hasTag(dbTag.getName(), true) && !entity.removeTag(dbTag)) {
                            validator.throwClientErrorException(entity.id, key, "Selected record can’t be tagged with ${dbTag.name}")
                        }
                    }
                }
                break
        }
        action
    }

}
