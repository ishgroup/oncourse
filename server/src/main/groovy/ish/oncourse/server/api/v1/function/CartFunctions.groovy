/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

class CartFunctions {
    public static final String CLASSES_KEY = "classes"
    public static final String WAITING_KEY = "waitingCourses"
    public static final String PRODUCTS_KEY = "products"

    private static Map<String, Class<? extends CayenneDataObject>> classes = new HashMap<String, Class<? extends CayenneDataObject>>() {
        {
            put(CLASSES_KEY, CourseClass.class)
            put(WAITING_KEY, WaitingList.class)
            put(PRODUCTS_KEY, Product.class)
        }
    }

    static CartDTO toRestCart(Checkout checkout) {
        new CartDTO().with { dto ->
            dto.createdOn = checkout.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            dto.totalValue = checkout.totalValue.toBigDecimal()
            dto.id = checkout.id
            dto
        }
    }

    static CartIdsDTO toRestCartIds(Checkout checkout) {
        new CartIdsDTO().with {dto ->
            dto.payerId = checkout.payer.id
            dto.contacts = checkout.contactRelations.groupBy {it.contact}.collect {toRestCartContactIds(it.key, it.value)}
            dto
        }
    }

    static CartContactIdsDTO toRestCartContactIds(Contact contact, List<CheckoutContactRelation> contactRelations){
        new CartContactIdsDTO().with {dto ->
            dto.contactId = contact.id
            dto.waitingCoursesIds = contactRelations.findAll {it instanceof CheckoutWaitingCourseRelation}
                                                    .collect {toRestCartObjectData(it)}
            dto.classIds = contactRelations.findAll {it instanceof CheckoutCourseClassRelation && it instanceof CheckoutApplicationRelation}
                    .collect {toRestCartObjectData(it)}
            dto.productIds = contactRelations.findAll {it instanceof CheckoutProductRelation}
                    .collect {toRestCartObjectData(it)}
            dto
        }
    }

    static CartObjectDataDTO toRestCartObjectData(CheckoutContactRelation contactRelation) {
        new CartObjectDataDTO().with {dto ->
            dto.id = contactRelation.relatedObjectId
            dto.selected = contactRelation.selected
            if(contactRelation instanceof CheckoutProductRelation) {
                def productRelation = contactRelation as CheckoutProductRelation
                dto.quantity = productRelation.quantity
                dto.productType = ProductTypeDTO.fromValue(TypesUtil.getEnumForDatabaseValue(productRelation.getRelatedObject().type, ProductType).toString())
            }
            dto
        }
    }

    static List<CheckoutContactRelation> checkoutsByContactId(ObjectContext context, Long contactId){
        return ObjectSelect.query(CheckoutContactRelation.class)
                .where(CheckoutContactRelation.CONTACT.dot(Contact.ID).eq(contactId))
                .select(context)
    }
}
