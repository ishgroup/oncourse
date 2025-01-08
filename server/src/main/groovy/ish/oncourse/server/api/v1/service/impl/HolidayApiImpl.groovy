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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.HolidayApi
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.ObjectContext

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.v1.function.HolidayFunctions.getHolidayById
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toDbHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.validateFoUpdate
import static ish.oncourse.server.api.v1.function.HolidayFunctions.validateForDelete

class HolidayApiImpl implements HolidayApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ISystemUserService systemUserService

    @Inject
    private UnavailableRuleDao unavailableRuleDao

    void setCayenneService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    void setUnavailableRuleDao(UnavailableRuleDao unavailableRuleDao) {
        this.unavailableRuleDao = unavailableRuleDao
    }

    @Override
    List<HolidayDTO> get() {
        unavailableRuleDao.getHolidays(cayenneService.newContext).collect {
            toRestHoliday(it)
        }
    }

    @Override
    void remove(String id) {
        ObjectContext context = cayenneService.newContext
        ValidationErrorDTO error = validateForDelete(context, id)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        context.deleteObject(getHolidayById(context, Long.valueOf(id)))
        context.commitChanges()
    }

    @Override
    void update(List<HolidayDTO> holiday) {
        ObjectContext context = cayenneService.newContext
        holiday.each { h ->
            ValidationErrorDTO error = validateFoUpdate(context, h, true)
            if (error) {
                context.rollbackChanges()
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
            toDbHoliday(context, h)
        }
        context.commitChanges()
    }
}
