/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.function.EntityFunctions
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.ChecklistsDTO
import ish.oncourse.server.api.v1.service.ChecklistApi
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject
import ish.util.EntityUtil

import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized

class ChecklistApiImpl implements ChecklistApi{
    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aqlService

    @Override
    ChecklistsDTO get(String entityName, Long id) {
        Class<? extends CayenneDataObject> objectClass = EntityUtil.entityClassForName(entityName)
        def taggable = EntityUtil.getObjectsByIds(cayenneService.newReadonlyContext, objectClass, List.of(id)).first()
        new ChecklistsDTO().with {
            it.allowedChecklists = TagFunctions.allowedChecklistsFor(taggable as TaggableCayenneDataObject, aqlService, cayenneService.newReadonlyContext).collect {toRestTagMinimized(it)}
            it.checkedChecklists = (taggable as TaggableCayenneDataObject).getChecklists().collect {it.id}
            it
        }
    }
}
