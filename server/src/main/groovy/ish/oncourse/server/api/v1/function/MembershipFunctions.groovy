/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.common.types.ProductStatus
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Membership
import ish.persistence.CommonExpressionFactory
import ish.util.DateTimeUtil
import ish.util.ProductUtil

class MembershipFunctions {

    static Date getRenwevalExpiryDate(Contact contact, final Membership membership) {
        Date expiry = CommonExpressionFactory.nextMidnightMinusOne(new Date())

        contact.memberships.findAll {
            it.getProduct().equalsIgnoreContext(membership.getProduct()) && ProductStatus.ACTIVE.equals(it.getStatus()) &&
                    !it.equalsIgnoreContext(membership) &&
                    it.getExpiryDate() != null &&
                    it.getExpiryDate().after(expiry)
        }.each {
            expiry.setTime(expiry.after(it.getExpiryDate()) ? expiry.getTime() : it.getExpiryDate().getTime())
        }

        if (DateTimeUtil.getDaysLeapYearDaylightSafe(new Date(), expiry) > 0) {
            return ProductUtil.calculateExpiryDate(expiry, membership.getProduct().getExpiryType(), membership.getProduct().getExpiryDays())
        }
        return null
    }
}
