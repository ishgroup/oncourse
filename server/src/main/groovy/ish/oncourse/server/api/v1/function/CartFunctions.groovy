/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.json.JsonSlurper
import ish.oncourse.server.api.v1.model.CartDTO
import ish.oncourse.server.api.v1.model.CartIdsDTO
import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.server.cayenne.glue.CayenneDataObject

import java.time.ZoneOffset

class CartFunctions {
    static CartDTO toRestCart(Checkout checkout){
        new CartDTO().with{dto ->
            dto.createdOn = checkout.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            dto.totalValue = checkout.totalValue.toBigDecimal()
            dto.id = checkout.id
            dto
        }
    }

    static CartIdsDTO cartDataIdsOf(Checkout checkout){
        def cartAsJson = checkout.shoppingCart
        def cartIds = new CartIdsDTO()
        if(cartAsJson == null)
            return cartIds

        def cartAsMap = new JsonSlurper().parseText(cartAsJson) as Map
        cartIds.payerId = parseAsLong(cartAsMap.payerId)

        /*def cartContacts = (cartAsMap.contacts as List<Map>)
        cartIds.contactIds = mapToAngelIds(cartContacts, "contactId")

        cartIds.classIds = mapToAngelIds(flatMapByKey(cartContacts, "classes"))
        cartIds.waitingCoursesIds = mapToAngelIds(flatMapByKey(cartContacts,"waitingCourses"))
        cartIds.productIds = mapToAngelIds(flatMapByKey(cartContacts,"products"))*/
        return cartIds
    }

    private static List<Map> flatMapByKey(List<Map> objects, String key){
        objects.collect {it.containsKey(key) ? it.get(key) : new ArrayList<>() as List<Map>}.flatten() as List<Map>
    }

    private static List<Long> mapToAngelIds(List<Map> objects, Class<? extends CayenneDataObject> angelClass, String key = "id"){
        objects.collect {toAngelId(parseAsLong(it.get(key)), angelClass)} as List<Long>
    }

    private static Long parseAsLong(Object value){
        Long.parseLong(value as String)
    }

    private static Long toAngelId(Long willowId, Class<? extends CayenneDataObject> angelClass){

    }
}
