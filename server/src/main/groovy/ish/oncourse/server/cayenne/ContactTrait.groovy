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

import ish.common.types.ProductStatus
import ish.oncourse.API
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.api.dao.PaymentInDao

import javax.annotation.Nullable
import java.time.LocalDate
import java.time.Period

trait ContactTrait {

    abstract List<Membership> getMemberships()
    abstract LocalDate getBirthDate()

    @Deprecated
    String getFull_name() {
        GetContactFullName.valueOf(this as Contact, true).get()
    }

    String getFullName(Boolean firstNameFirst) {
        GetContactFullName.valueOf(this as Contact, firstNameFirst).get()
    }


    boolean hasMembership(MembershipProduct membership) {
        memberships.any { it.status == ProductStatus.ACTIVE && it.expiryDate > new Date() && it.product.id == membership.id  }

    }

    /**
     * Get the age in whole years
     *
     * @param self
     * @return the contact's age
     */
    @API
    Integer getAge() {
        if (getBirthDate() != null) {
            return Period.between(getBirthDate(), LocalDate.now()).years
        } else {
            return null
        }
    }

    Boolean isHasSavedCC() {
        PaymentInDao.getCreditCardId(this as Contact) != null
    }

}
