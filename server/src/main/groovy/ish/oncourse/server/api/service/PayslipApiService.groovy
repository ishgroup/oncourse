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

import ish.oncourse.server.api.dao.PayslipDao
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.PayslipDTO
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.Tag
import org.apache.cayenne.ObjectContext

class PayslipApiService extends TaggableApiService<PayslipDTO, Payslip, PayslipDao> {
    @Override
    Class<Payslip> getPersistentClass() {
        return Payslip
    }

    @Override
    PayslipDTO toRestModel(Payslip cayenneModel) {
        return null
    }

    @Override
    Payslip toCayenneModel(PayslipDTO dto, Payslip cayenneModel) {
        return null
    }

    @Override
    void validateModelBeforeSave(PayslipDTO dto, ObjectContext context, Long id) {

    }

    @Override
    void validateModelBeforeRemove(Payslip cayenneModel) {

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
