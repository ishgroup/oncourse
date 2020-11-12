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

import groovy.transform.CompileStatic
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.DiscountMembershipDTO
import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.server.cayenne.DiscountMembershipRelationType
import org.apache.cayenne.ObjectContext

import java.util.stream.Collectors

@CompileStatic
class DiscountMembershipFunctions {

    static DiscountMembershipDTO toRestDiscountMembership(DiscountMembership dbDiscountMembership) {
        new DiscountMembershipDTO().with { dm ->
            dm.productId = dbDiscountMembership.membershipProduct.id
            dm.productName = dbDiscountMembership.membershipProduct.name
            dm.productSku = dbDiscountMembership.membershipProduct.sku
            dm.contactRelations = dbDiscountMembership.discountMembershipRelationTypes.stream().map{ relation ->
                relation.contactRelationType.id
            }.collect(Collectors.toList()) as List<Long>
            dm
        }
    }

    static void updateContactRelationTypes(ObjectContext context,
                                           DiscountMembership dbDiscountMembership,
                                           List<Long> relationsToSave) {

        context.deleteObjects(dbDiscountMembership.discountMembershipRelationTypes.findAll { !relationsToSave.contains(it.contactRelationType.id) })
        List<Long> contactRelationTypeIds = dbDiscountMembership.discountMembershipRelationTypes*.contactRelationType*.id.findAll()
        relationsToSave.findAll { !contactRelationTypeIds.contains(Long.valueOf(it)) }.each {
            context.newObject(DiscountMembershipRelationType).with { dmrt ->
                dmrt.discountMembership = dbDiscountMembership
                dmrt.contactRelationType = getRecordById(context, ContactRelationType.class, it as Long, null)
                dmrt
            }
        }
    }
}
