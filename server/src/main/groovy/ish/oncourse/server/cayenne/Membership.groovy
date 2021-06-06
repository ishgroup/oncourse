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

package ish.oncourse.server.cayenne

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Membership
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull

//TODO docs
/**
 * A persistent class mapped as "Membership" Cayenne entity.
 */
@API
@QueueableEntity
class Membership extends _Membership implements ExpandableTrait {

    @Override
    void validateForSave(@Nonnull ValidationResult result) {
        super.validateForSave(result)

        if (expiryDate == null) {
            result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_DATE.name, "Expiry date cannot be null."))
        } else if (newRecord && expiryDate < new Date()) {
            result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_DATE.name, "Expiry date should be in future."))
        }

        if (getStatus() == null) {
            result.addFailure(ValidationFailure.validationFailure(this, STATUS.name, "Status cannot be null."))
        }


    }

    @Override
    Class<? extends CustomField> getCustomFieldClass() {
        return MembershipCustomField
    }
}
