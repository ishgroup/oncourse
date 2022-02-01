/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.json.JsonSlurper
import ish.oncourse.server.api.v1.model.CartContactIdsDTO
import ish.oncourse.server.api.v1.model.CartDTO
import ish.oncourse.server.api.v1.model.CartIdsDTO
import ish.oncourse.server.api.v1.model.CartObjectDataDTO
import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

class CartFunctions {
    private static Map<String, Class<? extends CayenneDataObject>> classes = new HashMap<String, Class<? extends CayenneDataObject>>(){{
        put("classes", CourseClass)
        put("waitingCourses", WaitingList)
        put("products", Product)
    }}
    private static String jsonCartObjectIdKey = "id"

    static CartDTO toRestCart(Checkout checkout) {
        new CartDTO().with { dto ->
            dto.createdOn = checkout.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            dto.totalValue = checkout.totalValue.toBigDecimal()
            dto.id = checkout.id
            dto
        }
    }

    static CartIdsDTO cartDataIdsOf(Checkout checkout) {
        def cartAsJson = checkout.shoppingCart
        def cartIds = new CartIdsDTO()
        if (cartAsJson == null)
            return cartIds

        def cartAsMap = new JsonSlurper().parseText(cartAsJson) as Map

        new CartIdsDTO().with {it ->
            it.payerId = parseAsLong(cartAsMap.payerId)
            it.contacts = cartContactIdsOf(cartAsMap.contacts as List<Map>, checkout.getContext())
            it
        }
    }

    private static List<CartContactIdsDTO> cartContactIdsOf(List<Map> cartContacts, ObjectContext context){
        return cartContacts.collect {cartContactIdsOf(it, context)}
    }

    private static CartContactIdsDTO cartContactIdsOf(Map cartContact, ObjectContext context) {
        def cartContactIds = new CartContactIdsDTO()
        cartContactIds.contactId = parseAsLong(cartContact.contactId)

        cartContactIds.classIds = mapToCartObjects(cartContact, "classes", context)
        cartContactIds.productIds = mapToCartObjects(cartContact, "products", context)
        cartContactIds.waitingCoursesIds = mapToCartObjects(cartContact, "waitingCourses", context)
        cartContactIds
    }

    private static List<CartObjectDataDTO> mapToCartObjects(Map cartContact, String listKey, ObjectContext context){
       mapToSubList(cartContact, listKey).collect {mapToCartObject(it, classes.get(listKey), context)}
    }

    private static List<Map> mapToSubList(Map map, String listKey){
        map.containsKey(listKey) ? map.get(listKey) as List<Map> : new ArrayList<Map>()
    }

    private static CartObjectDataDTO mapToCartObject(Map data, Class<? extends CayenneDataObject> angelClass, ObjectContext context){
        new CartObjectDataDTO().with {it ->
            it.selected = data.selected
            it.id = toAngelId(parseAsLong(data.get(jsonCartObjectIdKey)), angelClass, context)
            it
        }
    }

    private static Long parseAsLong(Object value) {
        Long.parseLong(value as String)
    }

    private static Long toAngelId(Long willowId, Class<? extends CayenneDataObject> angelClass, ObjectContext context) {
        def willowIdProperty = Property.create("willowId", Long.class)
        def idProperty = Property.create("id", Long.class)
        return ObjectSelect.columnQuery(angelClass, idProperty)
                .where(willowIdProperty.eq(willowId))
                .selectOne(context) as Long
    }
}
