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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.v1.model.MembershipDiscountDTO
import ish.oncourse.server.cayenne.DiscountMembership

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class MembershipProductFunctions {

    static MembershipDiscountDTO toRestMembershipDiscount(DiscountMembership discountMembership) {
        new MembershipDiscountDTO().with { it ->
            it.discountId = discountMembership.discount.id
            it.discountName = discountMembership.discount.name
            it.applyToMemberOnly = discountMembership.applyToMemberOnly
            it.contactRelationTypes = discountMembership.discountMembershipRelationTypes.collect{ it.contactRelationType.id }
            it
        }
    }

    static LocalDate toLocalDate(Date date) {
        return  date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }

    static LocalDateTime toLocalDateTime(Date date) {
        return  date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    }

    static Date calculateNextDate(Integer day, Integer month) {
        Date now = new Date()
        Calendar calendar = Calendar.getInstance().with {
            it.setTime(now)
            it
        }
        Calendar nextDate = Calendar.getInstance().with { it ->
            it.setTime(now)
            it.set(MONTH, month)
            it.set(DAY_OF_MONTH, day)
            it
        }
        if (!nextDate.after(calendar)) {
            nextDate.add(Calendar.YEAR, 1)
        }
        return nextDate.getTime()
    }
}
