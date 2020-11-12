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

import ish.oncourse.server.api.dao.WaitingListDao
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.WaitingList
import org.apache.cayenne.ObjectContext

class WaitingListApiService extends TaggableApiService<WaitingListDTO, WaitingList, WaitingListDao> {
    @Override
    Class<WaitingList> getPersistentClass() {
        WaitingList
    }

    @Override
    WaitingListDTO toRestModel(WaitingList cayenneModel) {
        return null
    }

    @Override
    WaitingList toCayenneModel(WaitingListDTO dto, WaitingList cayenneModel) {
        return null
    }

    @Override
    void validateModelBeforeSave(WaitingListDTO dto, ObjectContext context, Long id) {

    }

    @Override
    void validateModelBeforeRemove(WaitingList cayenneModel) {

    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
